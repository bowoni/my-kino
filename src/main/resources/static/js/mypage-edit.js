/**
 * MyKino - 프로필 수정 페이지
 */
(function() {
    // 프로필 이미지 미리보기
    var fileInput = document.getElementById('profileImageFile');
    var avatarPreview = document.getElementById('avatarPreview');

    fileInput.addEventListener('change', function() {
        var file = this.files[0];
        if (!file) return;

        if (!file.type.startsWith('image/')) {
            alert('이미지 파일만 업로드할 수 있습니다.');
            this.value = '';
            return;
        }

        if (file.size > 5 * 1024 * 1024) {
            alert('파일 크기는 5MB 이하여야 합니다.');
            this.value = '';
            return;
        }

        var reader = new FileReader();
        reader.onload = function(e) {
            avatarPreview.innerHTML = '<img src="' + e.target.result + '" alt="프로필" id="avatarImg">';
        };
        reader.readAsDataURL(file);
    });

    // 닉네임 중복 체크
    var nicknameInput = document.getElementById('nickname');
    var nicknameHint = document.getElementById('nicknameHint');
    var originalNickname = nicknameInput.value;

    var checkNickname = MyKino.debounce(function() {
        var nickname = nicknameInput.value.trim();
        if (nickname.length < 2) {
            nicknameHint.textContent = '';
            nicknameHint.className = 'edit-hint';
            return;
        }
        if (nickname === originalNickname) {
            nicknameHint.textContent = '';
            nicknameHint.className = 'edit-hint';
            return;
        }

        MyKino.fetchJson('/api/public/check-nickname?nickname=' + encodeURIComponent(nickname))
            .then(function(available) {
                if (available) {
                    nicknameHint.textContent = '사용 가능한 닉네임입니다.';
                    nicknameHint.className = 'edit-hint valid';
                } else {
                    nicknameHint.textContent = '이미 사용 중인 닉네임입니다.';
                    nicknameHint.className = 'edit-hint invalid';
                }
            });
    }, 500);

    nicknameInput.addEventListener('input', checkNickname);

    // 자기소개 글자수 카운트
    var bioInput = document.getElementById('bio');
    var bioCount = document.getElementById('bioCount');

    bioInput.addEventListener('input', function() {
        bioCount.textContent = this.value.length;
    });
})();
