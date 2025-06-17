import React, { useState, useEffect, useRef } from "react";
import { useAuth } from "../../context/AuthContext";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import { FaPaperPlane, FaPaperclip } from "react-icons/fa";
import { toast } from "react-toastify";

const Chat = ({ onClose, consultationId }) => {
    const { user, login } = useAuth();
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState("");
    const [phoneNumber, setPhoneNumber] = useState(user ? user.phoneNumber : "");
    const [password, setPassword] = useState("");
    const [file, setFile] = useState(null);
    const stompClient = useRef(null);
    const messagesEndRef = useRef(null);
    const [isConnected, setIsConnected] = useState(false);

    useEffect(() => {
        const welcomeMessage = {
            from: "PTIT Hàng Châu",
            text: "PTIT Hàng Châu có thể hỗ trợ bạn chào Anh/Chi không?",
        };
        setMessages((prev) => (prev.length === 0 ? [welcomeMessage] : prev));
    }, []);

    useEffect(() => {
        if (!consultationId || !user || isConnected) return;

        const token = localStorage.getItem("token");
        if (!token) {
            console.warn("No token found, cannot connect to WebSocket");
            return;
        }

        const socket = new SockJS("http://localhost:8080/ws/websocket");
        stompClient.current = Stomp.over(socket);

        const connectCallback = () => {
            console.log("WebSocket connected successfully");
            setIsConnected(true);
            const subscribeFrame = [
                "SUBSCRIBE",
                `id:sub-${user.phoneNumber}`,
                `destination:/topic/consultation/${consultationId}`,
                "",
                "\x00",
            ].join("\n");
            stompClient.current.send(subscribeFrame);

            if (user && user.roleId === 3) {
                const greeting = `Xin chào, tôi là ${user.fullName || "Nhân viên"}, tôi có thể giúp gì cho bạn?`;
                const sendFrame = [
                    "SEND",
                    `destination:/app/chat.sendMessage/${consultationId}`,
                    "content-type:application/json",
                    "",
                    JSON.stringify({ content: greeting }),
                    "\x00",
                ].join("\n");
                stompClient.current.send(sendFrame);
                setMessages((prev) => [...prev, { from: user.phoneNumber, text: greeting }]);
            }

            stompClient.current.onmessage = (message) => {
                const data = JSON.parse(message.body);
                const sender = data.senderFullName || "Staff";
                const content = data.content;
                if (content === null) {
                    setMessages((prev) => [
                        ...prev,
                        { from: sender, text: "null", isError: true },
                    ]);
                } else {
                    setMessages((prev) => [
                        ...prev,
                        { from: sender, text: content },
                    ]);
                }
            };
        };

        const errorCallback = (error) => {
            console.error("WebSocket connection error:", error);
            setIsConnected(false);
            toast.error("Không thể kết nối chat, thử lại sau!", {
                position: "top-center",
                autoClose: 3000,
            });
        };

        stompClient.current.connect(
            { Authorization: `Bearer ${token}` },
            connectCallback,
            errorCallback
        );

        const reconnect = () => {
            if (!isConnected && (!stompClient.current || stompClient.current.ws.readyState === WebSocket.CLOSED)) {
                console.log("Attempting to reconnect...");
                stompClient.current = Stomp.over(new SockJS("http://localhost:8080/ws/websocket"));
                stompClient.current.connect(
                    { Authorization: `Bearer ${token}` },
                    connectCallback,
                    errorCallback
                );
            }
        };
        const reconnectInterval = setInterval(reconnect, 5000);

        return () => {
            if (stompClient.current && stompClient.current.connected) {
                console.log("Disconnecting WebSocket...");
                stompClient.current.disconnect();
            }
            clearInterval(reconnectInterval);
        };
    }, [consultationId, user]);

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }, [messages]);

    const startChat = async () => {
        if (user) return;

        try {
            if (!phoneNumber || !password) {
                toast.error("Vui lòng nhập số điện thoại và mật khẩu!", {
                    position: "top-center",
                    autoClose: 3000,
                });
                return;
            }

            const response = await fetch("http://localhost:8080/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ phoneNumber, password }),
            });
            const data = await response.json();
            if (response.ok) {
                login(data);
                setPhoneNumber(data.phoneNumber);
                toast.success("Đăng nhập thành công!", {
                    position: "top-center",
                    autoClose: 3000,
                });
            } else {
                const registerResponse = await fetch("http://localhost:8080/api/auth/register", {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ phoneNumber, password, fullName: "Guest" }),
                });
                const registerData = await registerResponse.json();
                if (registerResponse.ok) {
                    const loginResponse = await fetch("http://localhost:8080/api/auth/login", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ phoneNumber, password }),
                    });
                    const loginData = await loginResponse.json();
                    login(loginData);
                    setPhoneNumber(loginData.phoneNumber);
                    toast.success("Đăng ký và đăng nhập thành công!", {
                        position: "top-center",
                        autoClose: 3000,
                    });
                } else {
                    throw new Error("Registration failed");
                }
            }
        } catch (error) {
            console.error("Error during login/register:", error);
            toast.error("Đăng nhập thất bại, vui lòng thử lại!", {
                position: "top-center",
                autoClose: 3000,
            });
        }
    };

    const sendMessage = (e) => {
        e.preventDefault();
        if (!input.trim() || !stompClient.current || !isConnected) return;

        const sendFrame = [
            "SEND",
            `destination:/app/chat.sendMessage/${consultationId}`,
            "content-type:application/json",
            "",
            JSON.stringify({ content: input }),
            "\x00",
        ].join("\n");
        stompClient.current.send(sendFrame);
        setMessages((prev) => [...prev, { from: user ? user.phoneNumber : "You", text: input }]);
        setInput("");
    };

    const sendFile = async () => {
        if (!file || !stompClient.current || !isConnected) return;

        const reader = new FileReader();
        reader.onload = (e) => {
            const fileContent = e.target.result;
            const message = {
                content: `File: ${file.name}`,
                fileData: fileContent,
                fileName: file.name,
            };
            const sendFrame = [
                "SEND",
                `destination:/app/chat.sendMessage/${consultationId}`,
                "content-type:application/json",
                "",
                JSON.stringify(message),
                "\x00",
            ].join("\n");
            stompClient.current.send(sendFrame);
            toast.success("Gửi file thành công!", {
                position: "top-center",
                autoClose: 3000,
            });
            setMessages((prev) => [...prev, { from: user ? user.phoneNumber : "You", text: `File: ${file.name}` }]);
            setFile(null);
        };
        reader.onerror = () => {
            toast.error("Không thể đọc file, vui lòng thử lại!", {
                position: "top-center",
                autoClose: 3000,
            });
        };
        reader.readAsDataURL(file);
    };

    return (
        <div className="p-2">
            {!user && (
                <div className="mb-4">
                    <label className="block text-gray-700 dark:text-gray-300 mb-2">
                        Nhập số điện thoại của bạn
                    </label>
                    <input
                        type="tel"
                        value={phoneNumber}
                        onChange={(e) => setPhoneNumber(e.target.value)}
                        className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Nhập số điện thoại"
                    />
                    <label className="block text-gray-700 dark:text-gray-300 mb-2 mt-2">
                        Mật khẩu
                    </label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="w-full px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        placeholder="Nhập mật khẩu"
                    />
                    <button
                        onClick={startChat}
                        className="mt-2 bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 w-full"
                    >
                        Bắt Đầu
                    </button>
                </div>
            )}
            {(user || messages.length > 1) && (
                <>
                    <div className="h-64 overflow-y-auto mb-4 p-4 border rounded-lg dark:bg-gray-700 dark:text-gray-200">
                        {messages.map((msg, index) => (
                            <div
                                key={index}
                                className={`mb-2 ${msg.from === (user ? user.phoneNumber : "You") ? "text-right" : "text-left"}`}
                            >
                                <span className="font-bold">{msg.from}:</span>{" "}
                                <span className={msg.isError ? "text-red-500 font-mono" : ""}>
                                    {msg.text}
                                </span>
                            </div>
                        ))}
                        <div ref={messagesEndRef} />
                    </div>
                    <form onSubmit={sendMessage} className="flex items-center">
                        <input
                            type="text"
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            className="flex-1 px-3 py-2 border rounded-lg dark:bg-gray-700 dark:text-gray-200 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="Nhập tin nhắn..."
                        />
                        <label className="ml-2">
                            <input
                                type="file"
                                onChange={(e) => setFile(e.target.files[0])}
                                className="hidden"
                            />
                            <FaPaperclip className="text-blue-600 cursor-pointer" />
                        </label>
                        {file && (
                            <button
                                type="button"
                                onClick={sendFile}
                                className="ml-2 bg-blue-600 hover:bg-blue-700 text-white p-2 rounded-lg"
                            >
                                Gửi File
                            </button>
                        )}
                        <button
                            type="submit"
                            className="ml-2 bg-blue-600 hover:bg-blue-700 text-white p-2 rounded-lg"
                        >
                            <FaPaperPlane />
                        </button>
                    </form>
                </>
            )}
            <div className="text-center text-gray-500 text-sm mt-2">Powered by PTIT AI</div>
            <div className="flex justify-between mt-2">
                <button className="text-blue-600 hover:underline">Gửi yêu cầu</button>
                <button className="text-blue-600 hover:underline">Xem thêm</button>
            </div>
        </div>
    );
};

export default Chat;