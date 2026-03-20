/**
 * MyKino - AI 채팅
 */
(function() {
    // 탭 전환
    var tabs = document.querySelectorAll('.search-tab');
    var tabContents = document.querySelectorAll('.tab-content');

    for (var i = 0; i < tabs.length; i++) {
        tabs[i].addEventListener('click', function() {
            var tabName = this.getAttribute('data-tab');

            for (var j = 0; j < tabs.length; j++) {
                tabs[j].classList.remove('active');
            }
            for (var j = 0; j < tabContents.length; j++) {
                tabContents[j].classList.remove('active');
            }

            this.classList.add('active');
            document.getElementById('tab-' + tabName).classList.add('active');

            if (tabName === 'ai') {
                document.getElementById('aiChatInput').focus();
            }
        });
    }

    // AI 채팅
    var chatMessages = document.getElementById('aiChatMessages');
    var chatForm = document.getElementById('aiChatForm');
    var chatInput = document.getElementById('aiChatInput');
    var sendBtn = document.getElementById('aiSendBtn');
    var history = [];
    var isLoading = false;

    function addMessage(role, text) {
        var div = document.createElement('div');
        div.className = 'ai-message ' + (role === 'user' ? 'ai-user' : 'ai-assistant');

        var avatar = document.createElement('div');
        avatar.className = 'ai-avatar';
        avatar.textContent = role === 'user' ? 'You' : 'AI';

        var bubble = document.createElement('div');
        bubble.className = 'ai-bubble';
        bubble.innerHTML = formatText(text);

        div.appendChild(avatar);
        div.appendChild(bubble);
        chatMessages.appendChild(div);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    function addLoadingMessage() {
        var div = document.createElement('div');
        div.className = 'ai-message ai-assistant';
        div.id = 'aiLoading';

        var avatar = document.createElement('div');
        avatar.className = 'ai-avatar';
        avatar.textContent = 'AI';

        var bubble = document.createElement('div');
        bubble.className = 'ai-bubble ai-loading';
        bubble.innerHTML = '<span></span><span></span><span></span>';

        div.appendChild(avatar);
        div.appendChild(bubble);
        chatMessages.appendChild(div);
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    function removeLoadingMessage() {
        var loading = document.getElementById('aiLoading');
        if (loading) loading.remove();
    }

    function formatText(text) {
        // 줄바꿈 처리 + 기본 마크다운(**bold**)
        var escaped = MyKino.escapeHtml(text);
        escaped = escaped.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
        escaped = escaped.replace(/\n/g, '<br>');
        return escaped;
    }

    function setLoading(loading) {
        isLoading = loading;
        sendBtn.disabled = loading;
        chatInput.disabled = loading;
        if (!loading) chatInput.focus();
    }

    chatForm.addEventListener('submit', function(e) {
        e.preventDefault();

        var message = chatInput.value.trim();
        if (!message || isLoading) return;

        chatInput.value = '';
        addMessage('user', message);
        addLoadingMessage();
        setLoading(true);

        var requestBody = {
            message: message,
            history: history
        };

        fetch('/api/public/ai/chat', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        })
        .then(function(res) { return res.json(); })
        .then(function(data) {
            removeLoadingMessage();
            var reply = data.reply || '응답을 받을 수 없습니다.';
            addMessage('assistant', reply);

            history.push({ role: 'user', text: message });
            history.push({ role: 'assistant', text: reply });

            // 히스토리 최대 20개 유지 (10턴)
            if (history.length > 20) {
                history = history.slice(history.length - 20);
            }
        })
        .catch(function() {
            removeLoadingMessage();
            addMessage('assistant', '네트워크 오류가 발생했습니다. 다시 시도해주세요.');
        })
        .finally(function() {
            setLoading(false);
        });
    });
})();
