package ar.edu.itba.paw.webapp.controller;


import ar.edu.itba.paw.models.Album;
import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.Song;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.ReviewForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestMapping("/review")
@Controller
public class ReviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewService reviewService;
    private CommentService commentService;

    public ReviewController(ReviewService reviewService, CommentService commentService) {
        this.reviewService = reviewService;
        this.commentService = commentService;
    }

    @RequestMapping("/")
    public ModelAndView redirect() {
        return new ModelAndView("redirect:/");
    }

    @RequestMapping("/{reviewId:\\d+}")
    public ModelAndView review (@ModelAttribute("loggedUser") User loggedUser,
                                      @PathVariable(name = "reviewId") long reviewId){
        if (reviewService.isArtistReview(reviewId)) return artistReview(loggedUser, reviewId);
        if (reviewService.isAlbumReview(reviewId)) return albumReview(loggedUser, reviewId);
        if (reviewService.isSongReview(reviewId)) return songReview(loggedUser, reviewId);
        return new ModelAndView("not_found/review_not_found");
    }


    @RequestMapping("/artist/{reviewId:\\d+}")
    public ModelAndView artistReview (@ModelAttribute("loggedUser") User loggedUser,
                               @PathVariable(name = "reviewId") long reviewId){
        ModelAndView mav = new ModelAndView("reviews/review");
        ArtistReview review = reviewService.findArtistReviewById(reviewId).get();
        review.setLiked(reviewService.isLiked(loggedUser.getId(), reviewId));
        List<Comment> comments = commentService.findByReviewId(reviewId);

        mav.addObject("loggedUser", loggedUser);
        mav.addObject("review", review);
        mav.addObject("comments", comments);
        return mav;
    }

    @RequestMapping("/album/{reviewId:\\d+}")
    public ModelAndView albumReview (@ModelAttribute("loggedUser") User loggedUser,
                                      @PathVariable(name = "reviewId") long reviewId){
        ModelAndView mav = new ModelAndView("reviews/review");
        AlbumReview review = reviewService.findAlbumReviewById(reviewId).get();
        review.setLiked(reviewService.isLiked(loggedUser.getId(), reviewId));
        List<Comment> comments = commentService.findByReviewId(reviewId);

        mav.addObject("loggedUser", loggedUser);
        mav.addObject("review", review);
        mav.addObject("comments", comments);
        return mav;
    }

    @RequestMapping("/song/{reviewId:\\d+}")
    public ModelAndView songReview (@ModelAttribute("loggedUser") User loggedUser,
                                     @PathVariable(name = "reviewId") long reviewId){
        ModelAndView mav = new ModelAndView("reviews/review");
        SongReview review = reviewService.findSongReviewById(reviewId).get();
        review.setLiked(reviewService.isLiked(loggedUser.getId(), reviewId));
        List<Comment> comments = commentService.findByReviewId(reviewId);


        mav.addObject("loggedUser", loggedUser);
        mav.addObject("review", review);
        mav.addObject("isLiked", reviewService.isLiked(loggedUser.getId(), reviewId));
        mav.addObject("comments", comments);
        return mav;
    }

    @RequestMapping(value = "/like/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView createLike(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "reviewId") long reviewId) {
        reviewService.createLike(loggedUser.getId(), reviewId);
        LOGGER.info("User with ID {} liked review with ID {}", loggedUser.getId(), reviewId);
        return new ModelAndView("redirect:/review/" + reviewId);
    }

    @RequestMapping(value = "/remove-like/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView removeLike(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "reviewId") long reviewId) {
        reviewService.removeLike(loggedUser.getId(), reviewId);
        LOGGER.info("User with ID {} removed like from review with ID {}", loggedUser.getId(), reviewId);
        return new ModelAndView("redirect:/review/" + reviewId);
    }

    @PostMapping("/{reviewId:\\d+}/comment")
    public ModelAndView createComment(@PathVariable long reviewId, @RequestParam String content, @ModelAttribute("loggedUser") User loggedUser) {
        Comment comment = new Comment(loggedUser, reviewId, content);
        commentService.save(comment);
        return new ModelAndView("redirect:/review/" + reviewId);
    }

    @PostMapping("/{reviewId:\\d+}/comment/{commentId:\\d+}/delete")
    public ModelAndView deleteComment(@PathVariable long reviewId, @PathVariable long commentId, @ModelAttribute("loggedUser") User loggedUser) {
        Comment comment = commentService.findById(commentId).get();
        if (comment.getUser().getId().equals(loggedUser.getId()) || loggedUser.isModerator()) {
            commentService.deleteById(commentId);
        }
        return new ModelAndView("redirect:/review/" + reviewId);
    }
}
