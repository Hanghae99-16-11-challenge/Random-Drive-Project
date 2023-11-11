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
            'https://dapi.kakao.com/v2/local/geo/coord2address?x=' + lon + '&y=' + lat,
            {
                headers: { Authorization: 'KakaoAK 4752e5a5b955f574af7718613891f796' }, //rest api 키
            }
        )
            .then((response) => response.json())
            .then((data) => {
                console.log(data);
                if (data.documents && data.documents.length > 0) {
                    document.getElementById('all-random-originAddress').value = data.documents[0].address.address_name;
                } else {
                    throw new Error('Could not find address for this coordinates.');
                }
            });
    });
}
document.getElementById('random-current-location').addEventListener('click', handleCurrentLocationClick);

// 출발지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('random-search-origin').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('all-random-originAddress').value = data.address;
        }
    }).open();
});

// 목적지 주소 검색 버튼----------------------------------------------------------------------------------------------------//
document.getElementById('random-search-destination').addEventListener('click', function() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('random-destinationAddress').value = data.address;
        }
    }).open();
});

// 원하는 경유지 수 받아옴 -----------------------------------------------------------------------------------------------//
const selectWaypointNumber = document.getElementById("all-random-waypoint-number");
function getSelectedWaypointNumber() {
    const selectedValue = selectWaypointNumber.value;
    switch (selectedValue) {
        case "0":
            return 0;
        case "1":
            return 1;
        case "2":
            return 2;
        case "3":
            return 3;
        case "5":
            return 5;
        case "8":
            return 8;
        default:
            return 1;
    }
}

// 원하는 경로 타입 지정 ------------------------------------------------------------------------------------------------//
function getSelectedRouteType() {
    const selectElement = document.getElementById("all-random-type");
    const selectedValue = selectElement.value;
    switch (selectedValue) {
        case "line":
            return "line";
        case "zig-zeg":
            return "zigzag";
        default:
            return "original"; // 기본값으로 '평탄'을 반환합니다. 다른 기본값으로 설정하려면 해당 값을 반환하면 됩니다.
    }
}

// 사용자가 목적지 반경 길 찾기 버튼을 눌렀을 때
document.getElementById('random-search-form').addEventListener('submit', function(e) {
    e.preventDefault(); // 기본 submit 동작을 막습니다.

    const auth = getToken();

    if (auth !== undefined && auth !== '') {
        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', auth);
        });
    } else {
        window.location.href = host + '/view/user/login-page';
        return;
    }

    var originAddress = document.getElementById('all-random-originAddress').value;
    var destinationAddress = document.getElementById('random-destinationAddress').value;
    var waypointNum = getSelectedWaypointNumber();
    var secondType = getSelectedRouteType();

    {
        try {
            if (originAddress === "" || destinationAddress === "" || waypointNum === "" || secondType === "")
                throw new Error("값이 입력 되지 않았습니다.");
        }catch (e)
        {
            alert(e.message);
            return;
        }
    }

    window.location.href = 'navi/' + 'live-random' + '/0/' + originAddress + '/' + destinationAddress + '/10' + '/' + waypointNum + '/' + secondType;
});