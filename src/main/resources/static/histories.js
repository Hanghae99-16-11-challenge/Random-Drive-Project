//기능
$(document).ready(function() {
    const auth = getToken();

    if (auth !== undefined && auth !== '') {
        $.ajaxPrefilter(function(options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', auth);
        });

        // API 호출
        fetch('/api/routes', {
            method: 'GET',
            headers: {
                'Authorization': auth
            }})
            .then(response => response.json())
            .then(data => {
                // 받아온 데이터를 화면에 렌더링
                renderHistory(data);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    } else {
        window.location.href = host + '/api/user/login-page';
    }
});

// 보여질 정보 가져오기
function renderHistory(histories) {
    const tbody = document.getElementById('historyTableBody');

    histories.forEach(history => {
        const row = document.createElement('tr');
        row.innerHTML = `
                <td>${history.route_id}</td>
                <td>${history.originAddress}</td>
                <td>${history.destinationAddress}</td>
                <td>${history.duration}</td>
                <td>${history.distance}</td>
                <td>${history.createdAt}</td>
                <td></td> <!-- Actions 열의 빈 칸 -->
            `;

        // 상세 정보 버튼을 생성하고 Actions 열에 추가
        const detailButton = addDetailButton(history.route_id);
        row.lastElementChild.appendChild(detailButton);

        tbody.appendChild(row);
    });
}

// navigation.js로 데이터 상세정보 데이터 전송하는 버튼
function addDetailButton(routeId) {
    const button = document.createElement('button');
    button.textContent = '상세 정보 보기';
    button.addEventListener('click', function() {
        window.location.href = 'navi/' + 'save' + '/' + routeId + "/blank/blank/0";
    });
    return button;
}

// 토큰 가져오기
function getToken() {

    let auth = Cookies.get('Authorization');

    if(auth === undefined) {
        return '';
    }

    // kakao 로그인 사용한 경우 Bearer 추가
    if(auth.indexOf('Bearer') === -1 && auth !== ''){
        auth = 'Bearer ' + auth;
    }
    return auth;
}