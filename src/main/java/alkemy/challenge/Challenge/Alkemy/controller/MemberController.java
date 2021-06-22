package alkemy.challenge.Challenge.Alkemy.controller;

import alkemy.challenge.Challenge.Alkemy.model.Member;
import alkemy.challenge.Challenge.Alkemy.repository.MemberRepository;
import alkemy.challenge.Challenge.Alkemy.service.ErrorHandlingService;
import alkemy.challenge.Challenge.Alkemy.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/members")
public class MemberController {


    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    /*@GetMapping("/page")
    public ResponseEntity<?> getAll(@RequestParam(name = "page", defaultValue = "0") int page) {

        try {
            Pageable pageRequest = PageRequest.of(page, 10);
            Page<Member> memberPage = memberRepository.getMemberOfPage(pageRequest);
            return ResponseEntity.status(HttpStatus.OK).body(memberPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error. Please try again.");
        }
    }*/

    @GetMapping
    public Page<Member> listMembers(@PageableDefault(size = 10, page = 0) Pageable pageable) {
        Page<Member> result = memberService.findAll(pageable);
        return result;
    }


    @Autowired
    private ErrorHandlingService errorHandlingService;

    @PostMapping
    public ResponseEntity<?> newMember(@Valid @RequestBody Member member, BindingResult result) {

        Map<String, Object> response = new HashMap<>();
        errorHandlingService.registerErrorHandling(result, response);

        try {
            memberRepository.save(member);
        } catch (Exception e) {
            response.put("message", "ERROR DB");
            response.put("error_member", e.getMessage().concat(":"));
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        response.put("member", member);
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);

    }

    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable(value = "id") Long id) {
        Map<String, Object> response = new HashMap<>();

        if (id > 0) {
            Member member = memberRepository.findById(id).orElse(null);
            memberRepository.deleteById(id);
            if (member == null) {
                response.put("member_null", "Member doesn't exist");
            }
        } else {
            response.put("id_0", "The ID cannot be 0");
        }

    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editMember(@PathVariable(value = "id") Long id) {
        Map<String, Object> response = new HashMap<>();

        Member member = null;
        if (id > 0) {
            member = memberRepository.findById(id).orElse(null);
            memberRepository.save(member);
            if (member == null) {
                response.put("member_doesnt_exist", "Member with id " + id + " doesn't exist");
                return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
            }
        } else {
            response.put("id_0", "The ID cannot be 0");
            return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.ACCEPTED);
    }


}
