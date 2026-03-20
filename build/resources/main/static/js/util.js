/**
 * MyKino 공통 유틸리티
 */
var MyKino = MyKino || {};

MyKino.escapeHtml = function(text) {
    if (!text) return '';
    var div = document.createElement('div');
    div.appendChild(document.createTextNode(text));
    return div.innerHTML;
};

MyKino.fetchJson = function(url, options) {
    return fetch(url, options)
        .then(function(res) {
            if (res.status === 401) {
                location.href = '/login';
                return Promise.reject(new Error('인증이 필요합니다.'));
            }
            if (!res.ok) {
                return Promise.reject(new Error('HTTP ' + res.status));
            }
            return res.json();
        });
};

MyKino.debounce = function(fn, delay) {
    var timer;
    return function() {
        var args = arguments;
        var context = this;
        clearTimeout(timer);
        timer = setTimeout(function() {
            fn.apply(context, args);
        }, delay);
    };
};
