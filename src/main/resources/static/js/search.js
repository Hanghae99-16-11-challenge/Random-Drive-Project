const host = 'http://' + window.location.host;
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
// 사용자가 현재 내 위치 버튼을 클릭했을 때 동작
function handleCurrentLocationClick() {
    navigator.geolocation.getCurrentPosition(function(position) {
        var lat = position.coords.latitude;
        var lon = position.coords.longitude;

        fetch(
            'https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=' + lon + '&y=' + lat,
            {
                headers: { Authorization: 'KakaoAK 4752e5a5b955f574af7718613891f796' }, //rest api 키
            }
        )
            .then((response) => response.json())
            .then((data) => {
                if (data.documents && data.documents.length > 0) {
                    document.getElementById('originAddress').value = data.documents[0].address_name;
                } else {
                    throw new Error('Could not find address for this coordinates.');
                }
            });
    });
}
document.getElementById('current-location').addEventListener('click', handleCurrentLocationClick);

// 출발지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('search-origin').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('originAddress').value = data.address;
        }
    }).open();
});
// 목적지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('search-destination').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('destinationAddress').value = data.address;
        }
    }).open();
});

// 사용자가 길 찾기 버튼을 눌렀을 때
document.getElementById('search-form').addEventListener('submit', function(e) {
    e.preventDefault(); // 기본 submit 동작을 막습니다.

    const auth = getToken();

    if (auth !== undefined && auth !== '') {
        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', auth);
        });
    } else {
        window.location.href = host + '/api/user/login-page';
        return;
    }

    var originAddress = document.getElementById('originAddress').value;
    var destinationAddress = document.getElementById('destinationAddress').value;

    oriAddress = document.getElementById('originAddress').value;
    desAddress = document.getElementById('destinationAddress').value;
    console.log(oriAddress + " ~ " + desAddress);


    window.location.href = 'navi/' + 'live' + '/0/' + originAddress + '/' + destinationAddress + '/0';
});