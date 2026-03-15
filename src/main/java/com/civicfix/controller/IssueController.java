package com.civicfix.controller;

import com.civicfix.model.Issue;
import com.civicfix.model.User;
import com.civicfix.repository.IssueRepository;
import com.civicfix.repository.UserRepository;
import com.civicfix.service.SeverityService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class IssueController {

    private static final double COIMBATORE_LAT = 11.0168;
    private static final double COIMBATORE_LNG = 76.9558;
    private static final int RECENT_ISSUES_LIMIT = 6;
    private static final int ESCALATION_UPVOTES = 10;
    private static final int POINTS_REPORT = 10;
    private static final int POINTS_UPVOTE = 2;

    private final IssueRepository issueRepository;
    private final UserRepository userRepository;
    private final SeverityService severityService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public IssueController(IssueRepository issueRepository, UserRepository userRepository, SeverityService severityService) {
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
        this.severityService = severityService;
    }

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        addStatsToModel(model);
        List<Issue> recent = issueRepository.findAllByOrderByUpvotesDesc();
        if (recent.size() > RECENT_ISSUES_LIMIT) {
            recent = recent.subList(0, RECENT_ISSUES_LIMIT);
        }
        model.addAttribute("recentIssues", recent);
        model.addAttribute("loggedIn", session.getAttribute("userId") != null);
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userPoints", session.getAttribute("userPoints"));
        return "home";
    }

    @GetMapping("/report")
    public String reportForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to report an issue.");
            return "redirect:/login";
        }
        model.addAttribute("categories", List.of("Pothole", "Water Leak", "Sewage", "Streetlight", "Garbage", "Road Damage", "Other"));
        return "report";
    }

    @PostMapping("/report")
    public String report(
            @RequestParam String title,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String location,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) MultipartFile image,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to report an issue.");
            return "redirect:/login";
        }
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }
        if (title == null || title.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Title is required.");
            return "redirect:/report";
        }

        Issue issue = new Issue();
        issue.setTitle(title.trim());
        issue.setCategory(category != null ? category.trim() : "Other");
        issue.setDescription(description != null ? description.trim() : "");
        issue.setLocation(location != null ? location.trim() : "");
        issue.setLatitude(latitude != null ? latitude : 0);
        issue.setLongitude(longitude != null ? longitude : 0);
        issue.setReporterName(user.getName());
        issue.setReporterEmail(user.getEmail());
        issue.setUserId(userId);
        issue.setSeverity(severityService.calculateSeverity(issue.getCategory(), issue.getDescription()));
        issue.setStatus("Pending");

        if (image != null && !image.isEmpty()) {
            try {
                Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
                if (!Files.exists(dir)) Files.createDirectories(dir);
                String ext = Optional.ofNullable(image.getOriginalFilename())
                        .filter(s -> s.contains("."))
                        .map(s -> s.substring(s.lastIndexOf('.')))
                        .orElse(".jpg");
                String filename = "issue_" + System.currentTimeMillis() + ext;
                Path file = dir.resolve(filename);
                image.transferTo(file.toFile());
                issue.setImagePath("/uploads/" + filename);
            } catch (IOException e) {
                // continue without image
            }
        }

        issueRepository.save(issue);
        user.addPoints(POINTS_REPORT);
        userRepository.save(user);
        session.setAttribute("userPoints", user.getPoints());
        redirectAttributes.addFlashAttribute("success", "Issue reported successfully! +" + POINTS_REPORT + " points.");
        return "redirect:/issues";
    }

    @GetMapping("/issues")
    public String issues(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String severity,
            Model model,
            HttpSession session
    ) {
        List<Issue> list;
        if (status != null && !status.isBlank()) {
            list = issueRepository.findByStatus(status.trim());
        } else if (category != null && !category.isBlank()) {
            list = issueRepository.findByCategory(category.trim());
        } else if (severity != null && !severity.isBlank()) {
            list = issueRepository.findBySeverity(severity.trim());
        } else {
            list = issueRepository.findAllByOrderByUpvotesDesc();
        }
        model.addAttribute("issues", list);
        model.addAttribute("filterStatus", status);
        model.addAttribute("filterCategory", category);
        model.addAttribute("filterSeverity", severity);
        model.addAttribute("loggedIn", session.getAttribute("userId") != null);
        model.addAttribute("userId", session.getAttribute("userId"));
        model.addAttribute("userName", session.getAttribute("userName"));
        model.addAttribute("userPoints", session.getAttribute("userPoints"));
        return "issues";
    }

    @GetMapping("/map")
    public String map(Model model, HttpSession session) {
        addStatsToModel(model);
        Object userId = session != null ? session.getAttribute("userId") : null;
        model.addAttribute("loggedIn", userId != null);
        model.addAttribute("userName", session != null ? session.getAttribute("userName") : null);
        model.addAttribute("userPoints", session != null ? session.getAttribute("userPoints") : 0);
        return "map";
    }

    @GetMapping("/api/issues")
    @ResponseBody
    public List<Map<String, Object>> apiIssues() {
        List<Issue> all = issueRepository.findAllByOrderByUpvotesDesc();
        Random rnd = new Random(42);
        return all.stream().map(issue -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", issue.getId());
            m.put("title", issue.getTitle());
            m.put("category", issue.getCategory());
            m.put("description", issue.getDescription());
            m.put("location", issue.getLocation());
            m.put("status", issue.getStatus());
            m.put("severity", issue.getSeverity());
            m.put("upvotes", issue.getUpvotes());
            double lat = issue.getLatitude();
            double lng = issue.getLongitude();
            if (lat == 0 && lng == 0) {
                lat = COIMBATORE_LAT + (rnd.nextDouble() - 0.5) * 0.02;
                lng = COIMBATORE_LNG + (rnd.nextDouble() - 0.5) * 0.02;
            }
            m.put("latitude", lat);
            m.put("longitude", lng);
            return m;
        }).collect(Collectors.toList());
    }

    @PostMapping("/issues/upvote/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> upvote(@PathVariable String id, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        Map<String, Object> body = new HashMap<>();
        if (userId == null) {
            body.put("error", "Please log in to upvote.");
            return ResponseEntity.badRequest().body(body);
        }
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue == null) {
            body.put("error", "Issue not found.");
            return ResponseEntity.notFound().build();
        }
        if (issue.getUpvotedBy().contains(userId)) {
            body.put("upvotes", issue.getUpvotes());
            body.put("severity", issue.getSeverity());
            body.put("voted", true);
            return ResponseEntity.ok(body);
        }
        issue.getUpvotedBy().add(userId);
        issue.setUpvotes(issue.getUpvotes() + 1);
        if (issue.getUpvotes() >= ESCALATION_UPVOTES) {
            issue.setSeverity("Critical");
            issue.setStatus("In Progress");
        }
        issue.setUpdatedAt(LocalDateTime.now());
        issueRepository.save(issue);

        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.addPoints(POINTS_UPVOTE);
            userRepository.save(user);
            session.setAttribute("userPoints", user.getPoints());
        }

        body.put("upvotes", issue.getUpvotes());
        body.put("severity", issue.getSeverity());
        body.put("voted", true);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/admin")
    public String admin(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Admin only.");
            return "redirect:/";
        }
        addStatsToModel(model);
        model.addAttribute("allIssues", issueRepository.findAllByOrderByUpvotesDesc());
        model.addAttribute("leaderboard", userRepository.findAllByOrderByPointsDesc());
        return "admin";
    }

    @PostMapping("/admin/update/{id}")
    public String adminUpdate(
            @PathVariable String id,
            @RequestParam String status,
            @RequestParam String severity,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        if (!"ADMIN".equals(session.getAttribute("userRole"))) {
            redirectAttributes.addFlashAttribute("error", "Access denied.");
            return "redirect:/";
        }
        Issue issue = issueRepository.findById(id).orElse(null);
        if (issue != null) {
            if (status != null && !status.isBlank()) issue.setStatus(status.trim());
            if (severity != null && !severity.isBlank()) issue.setSeverity(severity.trim());
            issue.setUpdatedAt(LocalDateTime.now());
            issueRepository.save(issue);
            redirectAttributes.addFlashAttribute("success", "Issue updated.");
        }
        return "redirect:/admin";
    }

    private void addStatsToModel(Model model) {
        model.addAttribute("totalIssues", issueRepository.count());
        model.addAttribute("pendingCount", issueRepository.countByStatus("Pending"));
        model.addAttribute("inProgressCount", issueRepository.countByStatus("In Progress"));
        model.addAttribute("resolvedCount", issueRepository.countByStatus("Resolved"));
        model.addAttribute("criticalCount", issueRepository.countBySeverity("Critical"));
    }
}
