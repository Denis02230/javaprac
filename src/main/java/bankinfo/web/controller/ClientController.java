package bankinfo.web.controller;

import bankinfo.model.Account;
import bankinfo.model.Client;
import bankinfo.model.ClientType;
import bankinfo.web.form.ClientForm;
import bankinfo.web.service.ClientService;
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
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public String listClients(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "type", required = false) ClientType type,
            Model model
    ) {
        List<Client> clients = clientService.findClients(query, type);
        model.addAttribute("clients", clients);
        model.addAttribute("query", query == null ? "" : query);
        model.addAttribute("selectedType", type);
        model.addAttribute("allTypes", ClientType.values());
        return "p5-clients";
    }

    @GetMapping("/{id}")
    public String clientCard(@PathVariable("id") Long clientId, Model model) {
        Client client = clientService.findClientDetails(clientId)
                .orElseThrow(() -> new ValidationException("Client not found: " + clientId));
        List<Account> accounts = clientService.findClientAccounts(clientId);

        model.addAttribute("client", client);
        model.addAttribute("accounts", accounts);
        return "p6-client";
    }

    @GetMapping("/form")
    public String createClientForm(Model model) {
        ClientForm form = new ClientForm();
        form.setClientType(ClientType.PERSON);
        model.addAttribute("form", form);
        model.addAttribute("allTypes", ClientType.values());
        return "p7-client-form";
    }

    @GetMapping("/{id}/edit")
    public String editClientForm(@PathVariable("id") Long clientId, Model model) {
        Client client = clientService.findClientDetails(clientId)
                .orElseThrow(() -> new ValidationException("Client not found: " + clientId));

        ClientForm form = new ClientForm();
        form.setId(client.getId());
        form.setClientType(client.getClientType());
        form.setDisplayName(client.getDisplayName());

        model.addAttribute("form", form);
        model.addAttribute("allTypes", ClientType.values());
        return "p7-client-form";
    }

    @PostMapping("/save")
    public String saveClient(
            @ModelAttribute("form") ClientForm form,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            Client saved = clientService.saveClient(form.getId(), form.getClientType(), form.getDisplayName());
            redirectAttributes.addFlashAttribute("message", "Client saved");
            return "redirect:/clients/" + saved.getId();
        } catch (ValidationException exception) {
            model.addAttribute("form", form);
            model.addAttribute("allTypes", ClientType.values());
            model.addAttribute("errorMessage", exception.getMessage());
            return "p7-client-form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteClient(
            @PathVariable("id") Long clientId,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        try {
            clientService.deleteClient(clientId);
            redirectAttributes.addFlashAttribute("message", "Client deleted");
            return "redirect:/clients";
        } catch (ValidationException exception) {
            Client client = clientService.findClientDetails(clientId)
                    .orElseThrow(() -> exception);
            model.addAttribute("client", client);
            model.addAttribute("accounts", clientService.findClientAccounts(clientId));
            model.addAttribute("errorMessage", exception.getMessage());
            return "p6-client";
        }
    }
}
