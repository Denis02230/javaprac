package bankinfo.web.service;

import bankinfo.dao.AccountDao;
import bankinfo.dao.ClientDao;
import bankinfo.model.Account;
import bankinfo.model.Client;
import bankinfo.model.ClientType;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {

    private final ClientDao clientDao;
    private final AccountDao accountDao;

    public ClientService(ClientDao clientDao, AccountDao accountDao) {
        this.clientDao = clientDao;
        this.accountDao = accountDao;
    }

    public List<Client> findClients(String query, ClientType type) {
        List<Client> base;

        if (type != null) {
            base = clientDao.findByType(type);
        } else {
            base = clientDao.findAll();
        }

        if (query == null || query.isBlank()) {
            return base;
        }

        String normalized = query.trim().toLowerCase();
        List<Client> result = new ArrayList<>();
        for (Client client : base) {
            if (client.getDisplayName() != null && client.getDisplayName().toLowerCase().contains(normalized)) {
                result.add(client);
            }
        }
        return result;
    }

    public Optional<Client> findClientDetails(Long id) {
        return clientDao.findByIdDetailed(id);
    }

    public List<Account> findClientAccounts(Long clientId) {
        return accountDao.findByClientId(clientId);
    }

    public Client saveClient(Long id, ClientType clientType, String displayName) {
        if (clientType == null) {
            throw new ValidationException("Client type is required");
        }
        if (displayName == null || displayName.isBlank()) {
            throw new ValidationException("Display name is required");
        }

        Client client = id == null ? new Client() : clientDao.findById(id)
                .orElseThrow(() -> new ValidationException("Client not found: " + id));

        client.setClientType(clientType);
        client.setDisplayName(displayName.trim());
        if (client.getCreatedAt() == null) {
            client.setCreatedAt(OffsetDateTime.now());
        }
        return clientDao.save(client);
    }

    public void deleteClient(Long id) {
        Client client = clientDao.findById(id)
                .orElseThrow(() -> new ValidationException("Client not found: " + id));

        List<Account> accounts = accountDao.findByClientId(client.getId());
        if (!accounts.isEmpty()) {
            throw new ValidationException("Cannot delete client with existing accounts");
        }

        clientDao.deleteById(id);
    }
}
