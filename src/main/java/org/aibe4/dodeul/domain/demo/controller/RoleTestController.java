q/**
 * Role-based access control test controller.
 * For development/testing only.
 */
package org.aibe4.dodeul.domain.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoleTestController {

    @GetMapping("/mypage/mentor/test")
    public String mentorOnly() {
        return "mentor only endpoint";
    }

    @GetMapping("/mypage/mentee/test")
    public String menteeOnly() {
        return "mentee only endpoint";
    }
}
