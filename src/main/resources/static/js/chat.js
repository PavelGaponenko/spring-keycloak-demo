let stompClient = null;

function connect() {
    const socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            showMessage(JSON.parse(message.body));
        });
    });
}

function sendMessage() {
    const text = document.getElementById("text").value;
    if (text.trim() !== '') {
        stompClient.send("/app/chat", {}, JSON.stringify({'text': text}));
        addMessageToChat(text, true); // Отображение отправленного сообщения
        document.getElementById("text").value = ''; // Очистка поля
    }
}

function showMessage(message) {
    addMessageToChat(message.text, false);
}

function addMessageToChat(text, isSent) {
    const messagesDiv = document.getElementById("messages");
    const messageElement = document.createElement("div");
    messageElement.className = 'message ' + (isSent ? 'message-sent' : 'message-received');
    messageElement.textContent = text;
    messagesDiv.appendChild(messageElement);
    messagesDiv.scrollTop = messagesDiv.scrollHeight;
}

connect();