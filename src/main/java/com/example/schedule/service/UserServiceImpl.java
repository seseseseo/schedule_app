package com.example.schedule.service;

import com.example.schedule.config.PasswordEncoder;
import com.example.schedule.domain.entity.User;
import com.example.schedule.dto.UserRequestDto;
import com.example.schedule.dto.UserResponseDto;
import com.example.schedule.exception.CustomException;
import com.example.schedule.exception.ErrorCode;
import com.example.schedule.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor

/*
 * 사용자 관련 서비스 인터페이스
 * 회원가입, 로그인, 사용자 조회/삭제
 */
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * 이메일 중복 검사 후, 비밀번호 암호화 해서 저장
     */
    @Override
    public Long register(UserRequestDto dto) {
       if(userRepository.existsByEmail(dto.getEmail())) {
           throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
       }

       String encodePassword = passwordEncoder.encode(dto.getPassword());
       User users = User.of(dto.getUsername(), encodePassword, dto.getEmail());
       return userRepository.save(users).getId();
    }

    /**
     * 사용자 로그인 처리
     * 이메일 조회한 후, 비번 일치하면 id 반환, 실패하면 Optional.empty()
     */
    @Override
    public Optional<UserResponseDto> login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(UserResponseDto::new);
    }

    @Override
    @Transactional
    public void updateUserWithPasswordCheck(Long id, UserRequestDto dto, String password) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        boolean matched = passwordEncoder.matches(password, user.getPassword());
        if (!matched) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        if (!user.getEmail().equals(dto.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_CHANGE_NOT_ALLOWED);
        }
        user.updateUsername(dto.getUsername());
    }

    /**
     * 사용자 id로 사용자 조회
     * id 존재하면 DTO로 변환하여 반환
     */
    @Override
    public Optional<UserResponseDto> findById(Long id) {
        Optional<User> users = userRepository.findById(id);
        if (users.isPresent()) {
            UserResponseDto dto = new UserResponseDto(users.get());
            return Optional.of(dto);
        }
        return Optional.empty();
    }

    /**
     *  사용자 전체 조회
     */
    @Override
    public List<UserResponseDto> findAllUsers() {
        List<UserResponseDto> list = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            UserResponseDto userResponseDto = new UserResponseDto(user);
            list.add(userResponseDto);
        }
        return list;
    }


    /**
     * 사용자 삭제
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(id);
    }
}
