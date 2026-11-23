package com.example.tichhophethong.portal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal")
public class PortalController {

    private final PortalService portalService;

    public PortalController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/student-summary/{studentId}")
    public StudentSummary getStudentSummary(@PathVariable String studentId) {
        return portalService.getStudentSummary(studentId);
    }

    @GetMapping("/health")
    public ResponseEntity<PortalHealth> health() {
        return ResponseEntity.ok(new PortalHealth("UP"));
    }
}
