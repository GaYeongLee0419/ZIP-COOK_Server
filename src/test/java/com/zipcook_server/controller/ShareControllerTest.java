package com.zipcook_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zipcook_server.data.dto.share.ShareCreate;
import com.zipcook_server.data.dto.share.Sharedto;
import com.zipcook_server.data.entity.SharePost;
import com.zipcook_server.data.entity.User;
import com.zipcook_server.repository.Share.ShareRepository;
import com.zipcook_server.repository.UserRepository;
import com.zipcook_server.service.ShareService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class ShareControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private ShareService shareService;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void clean() {
        shareRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    @DisplayName("/share 요청시 db에 값이 저장된다")
    void test1() throws Exception {
        // given
        User user = User.builder()
                .id("joy")
                .email("example@example.com")
                .password("abc123")
                .location("seoul")
                .build();
        userRepository.save(user);

        ShareCreate shareCreate = ShareCreate.builder()
                .user(user)
                .title("tomato")
                .content("share tomato")
                .build();


        MockMultipartFile multipartFile1 = new MockMultipartFile("file", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        String json = objectMapper.writeValueAsString(shareCreate);
        MockMultipartFile sharepost = new MockMultipartFile("sharepost", "sharepost", "application/json", json.getBytes(StandardCharsets.UTF_8));


        mockMvc.perform(multipart("/board-share")
                        .file(multipartFile1)
                        .file(sharepost))
                .andExpect(status().isOk())
                .andDo(print());

        // then
        SharePost post = shareRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("tomato");
        assertThat(post.getContent()).isEqualTo("share tomato");
        assertThat(post.getUser().getId()).isEqualTo("joy");
    }



    @Test
    @DisplayName("글 1개 조회")
    void test2() throws Exception {
        //given
        User user = User.builder()
                .id("joy")
                .email("example@example.com")
                .password("abc123")
                .location("seoul")
                .build();
        userRepository.save(user);

        ShareCreate shareCreate = ShareCreate.builder()
                .user(user)
                .title("Test share post")
                .content("Test content")
                .regDate(new Date())
                .build();

        MockMultipartFile File= new MockMultipartFile("file", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        shareService.write(shareCreate,File);

        List<SharePost> sharePost=shareRepository.findByTitleContaining("share");


        //when
        mockMvc.perform(get("/board-share/{boardId}", sharePost.get(0).getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());


    }


    @Test
    @DisplayName("글 여러개 조회")
    void test3() throws Exception {
        // given
        User user = User.builder()
                .id("joy")
                .email("example@example.com")
                .password("abc123")
                .location("seoul")
                .build();
        userRepository.save(user);

        List<SharePost> requestPosts = IntStream.range(0, 10)
                .mapToObj(i -> SharePost.builder()
                        .user(user)
                        .title("title" + i)
                        .content("content" + i)
                        .build())
                .collect(Collectors.toList());

        shareRepository.saveAll(requestPosts);

        // expected
        mockMvc.perform(get("/board-share?page=1&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());


    }


    @Test
    @DisplayName("글 수정")
    void test4() throws Exception {
        // given
        User user = User.builder()
                .id("joy")
                .email("example@example.com")
                .password("abc123")
                .location("seoul")
                .build();
        userRepository.save(user);

        ShareCreate shareCreate = ShareCreate.builder()
                .user(user)
                .title("Test share post")
                .content("Test content")
                .regDate(new Date())
                .build();

        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        shareService.write(shareCreate, file);

        List<SharePost> sharePosts = shareRepository.findByTitleContaining("share");

        Sharedto update = Sharedto.builder()
                .user(user)
                .title("Test update post")
                .content("Test update content")
                .regDate(new Date())
                .build();

        String json = objectMapper.writeValueAsString(update);
        MockMultipartFile sharedto = new MockMultipartFile("update", "update", "application/json", json.getBytes(StandardCharsets.UTF_8));

        // when
        mockMvc.perform(multipart("/board-share/update/{boardId}", sharePosts.get(0).getId())
                        .file(file)
                        .file(sharedto))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated Successfully!"))
                .andDo(print());


    }



    @Test
    @DisplayName("게시글 삭제")
    void test5() throws Exception {
        User user = User.builder()
                .id("joy")
                .email("example@example.com")
                .password("abc123")
                .location("seoul")
                .build();
        userRepository.save(user);

        ShareCreate shareCreate = ShareCreate.builder()
                .user(user)
                .title("Test share post")
                .content("Test content")
                .regDate(new Date())
                .build();

        MockMultipartFile File= new MockMultipartFile("file", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        shareService.write(shareCreate,File);

        List<SharePost> sharePost=shareRepository.findByTitleContaining("share");

        //when
        mockMvc.perform(delete("/board-share/{boardId}", sharePost.get(0).getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());



    }


    @Test
    @DisplayName("/share 요청시 제목은 필수값이다")
    void test6() throws Exception {
        // given
        User user = User.builder()
                .id("joy")
                .email("example@example.com")
                .password("abc123")
                .location("seoul")
                .build();
        userRepository.save(user);

        ShareCreate shareCreate = ShareCreate.builder()
                .user(user)
                .content("share tomato")
                .build();


        MockMultipartFile multipartFile1 = new MockMultipartFile("file", "test.txt", "text/plain", "test file".getBytes(StandardCharsets.UTF_8));
        String json = objectMapper.writeValueAsString(shareCreate);
        MockMultipartFile sharepost = new MockMultipartFile("sharepost", "sharepost", "application/json", json.getBytes(StandardCharsets.UTF_8));


        mockMvc.perform(multipart("/board-share")
                        .file(multipartFile1)
                        .file(sharepost))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("잘못된 요청입니다"))
                .andExpect(jsonPath("$.validation.title").value("제목을 입력하세요"))
                .andDo(print());



    }

    @Test
    @DisplayName("게시글 검색")
    void test7() throws Exception {
        // given

        User user = User.builder()
                .id("joy")
                .email("example@example.com")
                .password("abc123")
                .location("seoul")
                .build();
        userRepository.save(user);

        SharePost sharePost = SharePost.builder()
                .user(user)
                .title("share tomato")
                .content("Test content")
                .regDate(new Date())
                .build();

        shareRepository.save(sharePost);

        SharePost sharePost2 = SharePost.builder()
                .user(user)
                .title("share banana")
                .content("Test content")
                .regDate(new Date())
                .build();

        shareRepository.save(sharePost2);

        // when
        mockMvc.perform(get("/board-share/search/{title}" ,"share")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

    }


}



