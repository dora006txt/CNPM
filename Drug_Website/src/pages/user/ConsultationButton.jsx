import React, { useState, useEffect } from "react";
import Chat from "./Chat";
import { useAuth } from "../../context/AuthContext";

const ConsultationButton = () => {
    const { user } = useAuth();
    const [isChatOpen, setIsChatOpen] = useState(false);
    const [consultationId, setConsultationId] = useState(null);

    const createConsultationRequest = async () => {
        try {
            const token = localStorage.getItem("token");
            const response = await fetch("http://localhost:8080/api/consultation-requests", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    requestType: "phone",
                    userMessage: "Tôi cần tư vấn",
                    branchId: null,
                }),
            });
            if (!response.ok) throw new Error("Failed to create consultation request");
            const data = await response.json();
            return data.requestId;
        } catch (error) {
            console.error("Error creating consultation request:", error);
            return null;
        }
    };

    const handleOpenChat = async () => {
        if (!user) {
            setIsChatOpen(true);
            return;
        }

        const id = await createConsultationRequest();
        if (id) {
            setConsultationId(id);
            setIsChatOpen(true);
        } else {
            setMessages((prev) => [
                ...prev,
                { from: "System", text: "requestId: null", isError: true },
            ]);
        }
    };

    return (
        <div className="fixed bottom-4 right-4 z-50">
            {!isChatOpen && (
                <button
                    onClick={handleOpenChat}
                    className="bg-blue-600 text-white px-4 py-2 rounded-full hover:bg-blue-700 flex items-center"
                >
                    Tư Vấn
                </button>
            )}
            {isChatOpen && (
                <div className="bg-white dark:bg-gray-800 rounded-lg shadow-lg w-80">
                    <div className="flex justify-between items-center p-2 border-b">
                        <h2 className="text-lg font-bold text-blue-600 dark:text-blue-400">
                            Chat với Dược Sĩ Hàng Châu
                        </h2>
                        <button
                            onClick={() => setIsChatOpen(false)}
                            className="text-gray-500 hover:text-gray-700"
                        >
                            ✕
                        </button>
                    </div>
                    <Chat onClose={() => setIsChatOpen(false)} consultationId={consultationId} />
                </div>
            )}
        </div>
    );
};

export default ConsultationButton;