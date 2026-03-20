package com.mykino.controller;

import com.mykino.config.CustomUserDetails;
import com.mykino.entity.User;
import com.mykino.entity.Watchlist;
import com.mykino.enums.WatchStatus;
import com.mykino.service.ReviewService;
import com.mykino.service.UserService;
import com.mykino.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final WatchlistService watchlistService;
    private final ReviewService reviewService;
    private final UserService userService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @GetMapping
    public String mypage(@AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);

        model.addAttribute("wantCount", watchlistService.countByStatus(user.getId(), WatchStatus.WANT_TO_WATCH));
        model.addAttribute("watchingCount", watchlistService.countByStatus(user.getId(), WatchStatus.WATCHING));
        model.addAttribute("watchedCount", watchlistService.countByStatus(user.getId(), WatchStatus.WATCHED));
        model.addAttribute("reviewCount", reviewService.countByUser(user.getId()));

        return "mypage/profile";
    }

    @GetMapping("/edit")
    public String editForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "mypage/edit";
    }

    @PostMapping("/edit")
    public String editProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestParam String nickname,
                              @RequestParam(required = false) String bio,
                              @RequestParam(required = false) MultipartFile profileImageFile,
                              RedirectAttributes redirectAttributes) {
        User user = userDetails.getUser();
        String profileImage = user.getProfileImage();

        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            try {
                String dir = System.getProperty("user.dir") + "/" + uploadDir + "/profiles";
                File uploadPath = new File(dir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs();
                }

                String originalName = profileImageFile.getOriginalFilename();
                String ext = originalName != null && originalName.contains(".")
                        ? originalName.substring(originalName.lastIndexOf("."))
                        : ".jpg";
                String fileName = user.getId() + "_" + System.currentTimeMillis() + ext;
                File dest = new File(dir, fileName);
                profileImageFile.transferTo(dest);

                profileImage = "/uploads/profiles/" + fileName;
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "이미지 업로드에 실패했습니다.");
                return "redirect:/mypage/edit";
            }
        }

        try {
            userService.updateProfile(user.getId(), nickname, bio, profileImage);

            // 세션의 SecurityContext 갱신
            User updatedUser = userService.findByEmail(user.getEmail());
            CustomUserDetails newDetails = new CustomUserDetails(updatedUser);
            UsernamePasswordAuthenticationToken newAuth =
                    new UsernamePasswordAuthenticationToken(newDetails, null, newDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            redirectAttributes.addFlashAttribute("success", "프로필이 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/mypage/edit";
        }

        return "redirect:/mypage";
    }

    @GetMapping("/watchlist")
    public String watchlist(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam(defaultValue = "WANT_TO_WATCH") String status,
                            Model model) {
        User user = userDetails.getUser();
        WatchStatus watchStatus = WatchStatus.valueOf(status);
        List<Watchlist> items = watchlistService.getMyWatchlistAll(user.getId(), watchStatus);

        model.addAttribute("user", user);
        model.addAttribute("items", items);
        model.addAttribute("currentStatus", watchStatus);
        model.addAttribute("wantCount", watchlistService.countByStatus(user.getId(), WatchStatus.WANT_TO_WATCH));
        model.addAttribute("watchingCount", watchlistService.countByStatus(user.getId(), WatchStatus.WATCHING));
        model.addAttribute("watchedCount", watchlistService.countByStatus(user.getId(), WatchStatus.WATCHED));

        return "mypage/watchlist";
    }

    @GetMapping("/reviews")
    public String reviews(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        User user = userDetails.getUser();
        model.addAttribute("user", user);
        model.addAttribute("reviews", reviewService.getMyReviews(user.getId(), PageRequest.of(page, 20)));

        return "mypage/reviews";
    }
}
