package bankinfo.web.controller;

import bankinfo.model.Branch;
import bankinfo.web.form.BranchForm;
import bankinfo.web.service.BranchService;
import bankinfo.web.service.ValidationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public String listBranches(@RequestParam(value = "q", required = false) String query, Model model) {
        List<Branch> branches = branchService.findBranches(query);
        model.addAttribute("branches", branches);
        model.addAttribute("query", query == null ? "" : query);
        return "p2-branches";
    }

    @GetMapping("/{id}")
    public String branchCard(@PathVariable("id") Long branchId, Model model) {
        Branch branch = branchService.findBranchDetails(branchId)
                .orElseThrow(() -> new ValidationException("Branch not found: " + branchId));
        model.addAttribute("branch", branch);
        return "p3-branch";
    }

    @GetMapping("/form")
    public String createBranchForm(Model model) {
        model.addAttribute("form", new BranchForm());
        return "p4-branch-form";
    }

    @GetMapping("/{id}/edit")
    public String editBranchForm(@PathVariable("id") Long branchId, Model model) {
        Branch branch = branchService.findBranchDetails(branchId)
                .orElseThrow(() -> new ValidationException("Branch not found: " + branchId));
        BranchForm form = new BranchForm();
        form.setId(branch.getId());
        form.setName(branch.getName());
        form.setAddress(branch.getAddress());
        model.addAttribute("form", form);
        return "p4-branch-form";
    }

    @PostMapping("/save")
    public String saveBranch(
            @ModelAttribute("form") BranchForm form,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            Branch saved = branchService.saveBranch(form.getId(), form.getName(), form.getAddress());
            redirectAttributes.addFlashAttribute("message", "Branch saved");
            return "redirect:/branches/" + saved.getId();
        } catch (ValidationException exception) {
            model.addAttribute("form", form);
            model.addAttribute("errorMessage", exception.getMessage());
            return "p4-branch-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBranch(
            @PathVariable("id") Long branchId,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            branchService.deleteBranch(branchId);
            redirectAttributes.addFlashAttribute("message", "Branch deleted");
            return "redirect:/branches";
        } catch (ValidationException exception) {
            Branch branch = branchService.findBranchDetails(branchId)
                    .orElseThrow(() -> exception);
            model.addAttribute("branch", branch);
            model.addAttribute("errorMessage", exception.getMessage());
            return "p3-branch";
        }
    }
}
