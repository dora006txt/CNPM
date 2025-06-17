import React, { useState, useEffect, useRef } from "react";
import SockJS from "sockjs-client";
import Stomp from "stompjs";
import Chat from "./Chat";
import { FaTimes } from "react-icons/fa";

const ConsultationButton = () => {
    const [isChatOpen, setIsChatOpen] = useState(false);
    const [consultationId, setConsultationId] = useState(null);
    const stompClient = useRef(null);

    useEffect(() => {
        const token = localStorage.getItem("token");
        if (!token) {
            console.warn("No token found, cannot connect to WebSocket");
            return;
        }

        const socket = new SockJS("http://localhost:8080/ws/websocket");
        stompClient.current = Stomp.over(socket);

        const connectCallback = () => {
            console.log("WebSocket connected for ConsultationButton");
            // Gửi yêu cầu tạo phòng chat
            const createRoomFrame = [
                "SEND",
                "destination:/app/chat.createRoom",
                "content-type:application/json",
                "",
                JSON.stringify({}),
                "\x00",
            ].join("\n");
            stompClient.current.send(createRoomFrame);

            // Subscribe để nhận consultationId từ server
            const subscribeFrame = [
                "SUBSCRIBE",
                "id:sub-create-room",
                "destination:/topic/consultation/create",
                "",
                "\x00",
            ].join("\n");
            stompClient.current.send(subscribeFrame);

            stompClient.current.onmessage = (message) => {
                const data = JSON.parse(message.body);
                if (data.consultationId) {
                    setConsultationId(data.consultationId);
                    setIsChatOpen(true); // Mở chat sau khi nhận consultationId
                }
            };
        };

        const errorCallback = (error) => {
            console.error("WebSocket connection error in ConsultationButton:", error);
        };

        stompClient.current.connect(
            { Authorization: `Bearer ${token}` },
            connectCallback,
            errorCallback
        );

        return () => {
            if (stompClient.current && stompClient.current.connected) {
                stompClient.current.disconnect();
                console.log("WebSocket disconnected in ConsultationButton");
            }
        };
    }, []);

    return (
        <>
            <button
                onClick={() => setIsChatOpen(true)}
                className="fixed bottom-4 right-4 bg-blue-600 text-white p-4 rounded-full shadow-lg hover:bg-blue-700 transition-colors"
            >
                Tư vấn ngay
            </button>

            {isChatOpen && consultationId && (
                <div className="fixed bottom-20 right-4 w-96 bg-white dark:bg-gray-800 rounded-lg shadow-lg">
                    <div className="flex justify-between items-center p-4 border-b dark:border-gray-700">
                        <h2 className="text-lg font-semibold">Tư vấn trực tuyến</h2>
                        <button
                            onClick={() => setIsChatOpen(false)}
                            className="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
                        >
                            <FaTimes />
                        </button>
                    </div>
                    <Chat onClose={() => setIsChatOpen(false)} consultationId={consultationId} />
                </div>
            )}
        </>
    );
};

export default ConsultationButton;