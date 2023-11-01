package com.example.randomdriveproject.history.entity;

import org.junit.jupiter.api.*;

//@EnableConfigurationProperties//테스트에서 property를 사용
//@SpringBootTest()//모든 빈 사용
public class JUnitCycleTest {
    @BeforeAll // 전체 테스트를 시작하기 전에 1회 실행하므로 메서드는 static으로 선언
    static void beforeAll() {
        System.out.println("@BeforeAll");
    }

    @BeforeEach // 테스트 케이스를 시작하기 전마다 실행
    public void beforeEach() {
        System.out.println("@BeforeEach");
    }

    @DisplayName("테스트 1")
    @Test
    public void test1() {
        System.out.println("test1");

        //give - when - then
        //give : 값 준비 , when : 값 적용 , then : 검증
//        Assertions.assertEquals(0, 0);//값이 같은지 확인
    }

    @Test
    public void test2() {
        System.out.println("test2");
    }

    @Test
    public void test3() {
        System.out.println("test3");
    }

    @AfterAll // 전체 테스트를 마치고 종료하기 전에 1회 실행하므로 메서드는 static으로 선언
    static void afterAll() {
        System.out.println("@AfterAll");
    }

    @AfterEach // 테스트 케이스를 종료하기 전마다 실행
    public void afterEach() {
        System.out.println("@AfterEach");
    }
}