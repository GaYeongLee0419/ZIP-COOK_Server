package com.zipcook_server.service;

import com.zipcook_server.data.dto.comment.CommentCreate;
import com.zipcook_server.data.dto.comment.SaleCommentdto;
import com.zipcook_server.data.dto.comment.ShareCommentdto;
import com.zipcook_server.data.entity.Comment.SaleComment;
import com.zipcook_server.data.entity.Comment.ShareComment;
import com.zipcook_server.data.entity.SalePost;
import com.zipcook_server.data.entity.SharePost;
import com.zipcook_server.data.entity.User;
import com.zipcook_server.exception.PostNotFound;
import com.zipcook_server.repository.Comment.SaleCommentRepository;
import com.zipcook_server.repository.Comment.ShareCommentRepository;
import com.zipcook_server.repository.Recipe.RecipeRepository;
import com.zipcook_server.repository.Sale.SaleRepository;
import com.zipcook_server.repository.Share.ShareRepository;
import com.zipcook_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    RecipeRepository recipeRepository;
    @Autowired
    SaleRepository saleRepository;
    @Autowired
    ShareRepository shareRepository;
    @Autowired
    ShareCommentRepository shareCommentRepository;

    @Autowired
    SaleCommentRepository saleCommentRepository;
    @Autowired
    UserRepository userRepository;

    ///////share댓글/////////////////////////////
    @Transactional
    public void sharecommentsave(CommentCreate commentCreate){

        if (commentCreate == null) {
            throw new IllegalArgumentException("CommentCreate cannot be null.");
        }

        SharePost post=shareRepository.findById(commentCreate.getBoard_id())
                .orElseThrow(PostNotFound::new);


        User user = userRepository.findById(commentCreate.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        ShareComment shareComment = ShareComment.builder()
                .writer(commentCreate.getWriter())
                .content(commentCreate.getContent())
                .user(user)
                .sharePost(post)
                .regDate(new Date())
                .build();

        shareCommentRepository.save(shareComment);
    }

    public List<ShareCommentdto> sharecommentfindall(Long sharepost_id){
        return shareCommentRepository.findBySharePostIdOrderByIdDesc(sharepost_id).stream()
                .map(ShareCommentdto::new)
                .collect(Collectors.toList());
    }


    @Transactional
    public void sharecommentupdate(Long id, ShareCommentdto update) throws IOException {
        ShareComment shareComment=shareCommentRepository.findById(id)
                .orElseThrow(PostNotFound::new);
        shareComment.toUpdateEntity(update);
        shareCommentRepository.save(shareComment);
    }

    public void sharecommentdelete(Long id){
        ShareComment shareComment =shareCommentRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        shareCommentRepository.delete(shareComment);
    }


    ///////sale댓글/////////////////////////////
    @Transactional
    public void salecommentsave(CommentCreate commentCreate){

        if (commentCreate == null) {
            throw new IllegalArgumentException("CommentCreate cannot be null.");
        }

        SalePost post=saleRepository.findById(commentCreate.getBoard_id())
                .orElseThrow(PostNotFound::new);


        User user = userRepository.findById(commentCreate.getUser_id())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user id"));

        SaleComment saleComment = SaleComment.builder()
                .writer(commentCreate.getWriter())
                .content(commentCreate.getContent())
                .user(user)
                .salePost(post)
                .regDate(new Date())
                .build();

        saleCommentRepository.save(saleComment);
    }

    public List<SaleCommentdto> salecommentfindall(Long salepost_id){
        return saleCommentRepository.findBySalePostIdOrderByIdDesc(salepost_id).stream()
                .map(SaleCommentdto::new)
                .collect(Collectors.toList());
    }


    @Transactional
    public void salecommentupdate(Long id, SaleCommentdto update) throws IOException {
        SaleComment saleComment=saleCommentRepository.findById(id)
                .orElseThrow(PostNotFound::new);
        saleComment.toUpdateEntity(update);
        saleCommentRepository.save(saleComment);
    }

    public void salecommentdelete(Long id){
        SaleComment saleComment =saleCommentRepository.findById(id)
                .orElseThrow(PostNotFound::new);

        saleCommentRepository.delete(saleComment);
    }
}
