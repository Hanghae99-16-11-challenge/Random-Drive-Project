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
        const formattedOriginAddress = extractAddressParts(history.originAddress);
        const formattedDestinationAddress = extractAddressParts(history.destinationAddress);

        row.innerHTML = `
                <td>${extractDateFromDateTime(history.createdAt)}</td>
                <td>${formattedOriginAddress}</td>
                <td>${formattedDestinationAddress}</td>
                <td>${formatDistance(history.distance)}
                ${formatDuration(history.duration)}</td>
            `;

        row.addEventListener('click', function() {
            // 클릭한 행의 route_id를 사용하여 원하는 동작 수행
            window.location.href = 'navi/' + 'save' + '/' + history.route_id + "/blank/blank/0";
        });

        tbody.appendChild(row);
    });
}

// 날자만 뜨도록 설정
function extractDateFromDateTime(dateTimeString) {
    const dateTime = new Date(dateTimeString);
    const year = dateTime.getFullYear();
    const month = String(dateTime.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 1을 더하고 문자열로 변환
    const day = String(dateTime.getDate()).padStart(2, '0'); // 날짜를 문자열로 변환
    return `${year}-${month}-${day}`;
}

// 킬로미터로 변환
function formatDistance(distance) {
    const kilometers = distance / 1000; // 미터를 킬로미터로 변환
    return kilometers.toFixed(1) + ' km'; // 소수점 첫 번째 자리까지 나타내기
}

// 시간 분 형식으로 시간을 변환
function formatDuration(duration) {
    const hours = Math.floor(duration / 3600); // 초를 시간으로 변환
    const minutes = Math.floor((duration % 3600) / 60); // 초를 분으로 변환하고 소수점 첫 번째 자리까지 나타내기
    return `${hours}시간${minutes}분`;
}

// 주소 짧게 줄임
function extractAddressParts(address) {
    const parts = address.split(' ');
    if (parts.length >= 3) {
        return `${parts[1]} ${parts[2]}`;
    }
    return address; // 두 번째와 세 번째 부분이 없는 경우 전체 주소 반환
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