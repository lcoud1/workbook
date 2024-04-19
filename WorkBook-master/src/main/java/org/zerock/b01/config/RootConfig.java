package org.zerock.b01.config;


import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration // 스프링의 설정 클래스임을 명시
public class RootConfig {

    // 모델 영역에 데이터 매핑용
    @Bean
    public ModelMapper getMapper() {
        // ModelMapper 서로 다른 클래스의 값을 한 번에 복사하게 도와주는 라이브러리
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()  // 기본 환경설정을 가져옴.
                .setFieldMatchingEnabled(true) // private 인 필드도 맵핑
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE) //Private 필드 매핑
                .setMatchingStrategy(MatchingStrategies.STRICT);
                //MatchingStrategies.STANDARD(default)
                    //모든 destination 객체의 property 토큰들은 매칭 되어야 한다.
                    //모든 source 객체의 property들은 하나 이상의 토큰이 매칭되어야 한다.
                    //토큰은 어떤 순서로든 일치될 수 있다.
                //MatchingStrategies.STRICT
                    //가장 엄격한 전략
                    //source와 destination의 타입과 필드명이 같을 때만 변환
                    //의도하지 않은 매핑이 일어나는 것을 방지할 때 사용
                //MatchingStrategies.LOOSE
                    //가장 느슨한 전략
                    //토큰을 어떤 순서로도 일치 시킬 수 있다.
                    //마지막 destination 필드명은 모든 토큰이 일치해야 한다.
                    //마지막 source 필드명에는 일치하는 토큰이 하나 이상 있어야 한다.

        return modelMapper;
    }
}
