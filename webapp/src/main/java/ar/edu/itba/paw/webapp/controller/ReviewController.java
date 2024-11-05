package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Comment;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.reviews.AlbumReview;
import ar.edu.itba.paw.models.reviews.ArtistReview;
import ar.edu.itba.paw.models.reviews.Review;
import ar.edu.itba.paw.models.reviews.SongReview;
import ar.edu.itba.paw.services.*;
import ar.edu.itba.paw.webapp.form.CommentForm;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@RequestMapping("/review")
@Controller
public class ReviewController {

    private final ReviewService reviewService;
    private final CommentService commentService;
    private static final int PAGE_SIZE = 10;

    public ReviewController(ReviewService reviewService, CommentService commentService) {
        this.reviewService = reviewService;
        this.commentService = commentService;
    }

    @RequestMapping("/")
    public ModelAndView redirect() {
        return new ModelAndView("redirect:/");
    }
    @RequestMapping(value = "/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView review(@ModelAttribute("loggedUser") User loggedUser, 
                             @PathVariable(value = "reviewId", required = true) Long reviewId,
                             @RequestParam(name = "page", required = false, defaultValue = "comments") String page,
                             @RequestParam(name = "pageNum", required = false, defaultValue = "1") Integer pageNum) {
        ModelAndView mav = new ModelAndView("reviews/review");
        Optional<Review> reviewOptional = reviewService.find(reviewId);
        if (reviewOptional.isEmpty()) return new ModelAndView("not_found/review_not_found");
        Review review = reviewOptional.get();
        review.setLiked(reviewService.isLiked(loggedUser.getId(), reviewId));

        // Configurar paginación
        boolean showNext;
        boolean showPrevious = pageNum > 1;
        boolean likesActive = "likes".equals(page);

        if (likesActive) {
            List<User> likedUsers = reviewService.likedBy(reviewId, pageNum, PAGE_SIZE);
            showNext = likedUsers.size() == PAGE_SIZE;
            
            mav.addObject("likedUsers", likedUsers);
        } else {
            List<Comment> comments = commentService.findByReviewId(reviewId, PAGE_SIZE, pageNum);
            showNext = comments.size() == PAGE_SIZE;
            
            mav.addObject("comments", comments);
            mav.addObject("commentForm", new CommentForm());
        }

        mav.addObject("loggedUser", loggedUser);
        mav.addObject("review", review);
        mav.addObject("pageNum", pageNum);
        mav.addObject("showNext", showNext);
        mav.addObject("showPrevious", showPrevious);
        mav.addObject("likesActive", likesActive);

        return mav;
    }

    @RequestMapping(value = "/like/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView createLike(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "reviewId") long reviewId) {
        if (loggedUser.getId() == 0) return new ModelAndView("redirect:/user/login");
        reviewService.createLike(loggedUser.getId(), reviewId);
        return new ModelAndView("redirect:/review/" + reviewId);
    }

    @RequestMapping(value = "/remove-like/{reviewId:\\d+}", method = RequestMethod.GET)
    public ModelAndView removeLike(@ModelAttribute("loggedUser") User loggedUser, @PathVariable(name = "reviewId") long reviewId) {
        if (loggedUser.getId() == 0) return new ModelAndView("redirect:/user/login");
        reviewService.removeLike(loggedUser.getId(), reviewId);
        return new ModelAndView("redirect:/review/" + reviewId);
    }

    @RequestMapping(value = "/{reviewId:\\d+}/comment", method = RequestMethod.POST)
    public ModelAndView createComment(@PathVariable long reviewId, @Valid @ModelAttribute("commentForm") CommentForm commentForm, BindingResult bindingResult, @ModelAttribute("loggedUser") User loggedUser) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("redirect:/review/" + reviewId);
        }
        if (loggedUser.getId() == 0) return new ModelAndView("redirect:/user/login");
        Optional<Review> reviewOptional = reviewService.find(reviewId);
        Comment comment = new Comment(loggedUser, reviewOptional.get(), commentForm.getContent());
        commentService.save(comment);
        return new ModelAndView("redirect:/review/" + reviewId);
    }

    @RequestMapping("/{reviewId:\\d+}/comment/{commentId:\\d+}/delete")
    public ModelAndView deleteComment(@PathVariable long reviewId, @PathVariable long commentId, @ModelAttribute("loggedUser") User loggedUser) {
        Comment comment = commentService.findById(commentId).get();
        if (comment.getUser().getId().equals(loggedUser.getId()) || loggedUser.isModerator()) {
            commentService.deleteById(commentId);
        }
        return new ModelAndView("redirect:/review/" + reviewId);
    }
}
