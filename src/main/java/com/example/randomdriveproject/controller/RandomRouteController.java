package com.example.randomdriveproject.controller;


import com.example.randomdriveproject.navigation.random.service.RandomOffCourseService;
import com.example.randomdriveproject.navigation.random.service.RandomKakaoRouteSearchService;
import com.example.randomdriveproject.navigation.random.service.RealRandomRouteSearchService;
import com.example.randomdriveproject.request.dto.KakaoRouteAllResponseDto;
import com.example.randomdriveproject.user.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Random Route Controller", description = "랜덤 경로 네비게이션")
@Controller
@RequiredArgsConstructor
@Slf4j(topic = "RouteController")
public class RandomRouteController {

    private final RandomKakaoRouteSearchService kakaoRouteSearchService;
    private final RealRandomRouteSearchService realRandomRouteSearchService;
    private final RandomOffCourseService randomOffCourseService;

    @GetMapping("/all-random-route")
    @Operation(summary = "랜덤경로", description = "반경을 기준으로 랜덤 경로를 가져옵니다.")
    public ResponseEntity<KakaoRouteAllResponseDto> getRandomWays(@RequestParam String originAddress, @RequestParam Integer redius, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        KakaoRouteAllResponseDto response = kakaoRouteSearchService.requestAllRandomWay(userDetails.getUsername(),originAddress,redius);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 적절한 HTTP 상태 코드로 응답
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/random-route")
    @Operation(summary = "랜덤경로", description = "목적지와 반경을 설정한 후 랜덤 경로를 가져옵니다.")
    public ResponseEntity<KakaoRouteAllResponseDto> getRandomWay(@RequestParam String originAddress,@RequestParam String destinationAddress, @RequestParam Integer redius) {
        KakaoRouteAllResponseDto response = kakaoRouteSearchService.requestRamdomWay(originAddress,destinationAddress,redius);

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 적절한 HTTP 상태 코드로 응답
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/line-random-route")
    @Operation(summary = "개선된 랜덤경로", description = "목적지와 반경을 설정한 후 랜덤 경로를 가져옵니다.")
    public ResponseEntity<KakaoRouteAllResponseDto> getRealRandomWay(@RequestParam String originAddress,@RequestParam String destinationAddress, @RequestParam Integer count) {
        KakaoRouteAllResponseDto response = realRandomRouteSearchService.requestRandomWay(originAddress,destinationAddress,count);

        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 적절한 HTTP 상태 코드로 응답
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/line-all-random-route")
    @Operation(summary = "개선된 랜덤경로", description = "반경을 기준으로 랜덤 경로를 가져옵니다.")
    public ResponseEntity<KakaoRouteAllResponseDto> getLineRandomWays(@RequestParam String originAddress, @RequestParam Integer distance,
                                                                      @RequestParam Integer count, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        KakaoRouteAllResponseDto response = realRandomRouteSearchService.requestAllRandomWay(userDetails.getUsername(), originAddress, distance, count);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 적절한 HTTP 상태 코드로 응답
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/box-all-random-route")
    @Operation(summary = "개선된 랜덤경로", description = "반경을 기준으로 랜덤 경로를 가져옵니다.")
    public ResponseEntity<KakaoRouteAllResponseDto> getBoxRandomWays(@RequestParam String originAddress, @RequestParam Integer distance,
                                                                      @RequestParam Integer count, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        KakaoRouteAllResponseDto response = realRandomRouteSearchService.requestAllRandomWay(userDetails.getUsername(), originAddress, distance, count);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 적절한 HTTP 상태 코드로 응답
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/offCourse")
    public ResponseEntity<KakaoRouteAllResponseDto> getOffCourseWays(@RequestParam double originY, @RequestParam double originX,
                                                                     @RequestParam double destinationY, @RequestParam double destinationX,
                                                                     @RequestParam String waypointsY, @RequestParam String waypointsX) {
        KakaoRouteAllResponseDto response = randomOffCourseService.requestOffCourseSearch(originY, originX, destinationY, destinationX, waypointsY, waypointsX);
        if (response == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 적절한 HTTP 상태 코드로 응답
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
