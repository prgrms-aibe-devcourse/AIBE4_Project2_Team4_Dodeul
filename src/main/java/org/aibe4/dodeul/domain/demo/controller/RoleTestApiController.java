/** Role-based access control test controller. For development/testing only. */
package org.aibe4.dodeul.domain.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo/role")
public class RoleTestApiController {

    @GetMapping("/mentor")
    public String mentorOnly() {
        return "mentor only endpoint";
    }

    @GetMapping("/mentee")
    public String menteeOnly() {
        return "mentee only endpoint";
    }
}
