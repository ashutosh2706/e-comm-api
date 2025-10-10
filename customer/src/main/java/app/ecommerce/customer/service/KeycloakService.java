package app.ecommerce.customer.service;

import app.ecommerce.customer.entity.Role;
import app.ecommerce.customer.exception.KeyCloakServiceException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class KeycloakService {

    private final Keycloak keycloak;
    private final String realm;
    private final String clientId;
    private final String authClientId;

    public KeycloakService(@Value("${kc.config.server-url}") String serverUrl, @Value("${kc.config.realm}") String realm, @Value("${kc.config.service-client-id}") String clientId, @Value("${kc.config.service-client-secret}") String clientSecret, @Value("${kc.config.auth-client-id}") String authClientId) {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
        this.realm = realm;
        this.clientId = clientId;
        this.authClientId = authClientId;
    }

    public String createUser(String firstName, String lastName, String email) throws KeyCloakServiceException {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(firstName.trim().toLowerCase() + "." + lastName.trim().toLowerCase());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setEmailVerified(true);
        user.setEnabled(true);

        String userId = "";
        try (Response response = keycloak.realm(realm).users().create(user)) {
            if (response.getStatus() != 201) {
                throw new KeyCloakServiceException("KeyCloak User Creation Failed. Status: "+response.getStatus());
            }
            userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        } catch (Exception e) {
            throw new KeyCloakServiceException(e.getMessage());
        }

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("123");
        credential.setTemporary(false);
        keycloak.realm(realm).users().get(userId).resetPassword(credential);
        return userId;
    }

    public void assignRole(Role role, String userId) throws KeyCloakServiceException {
        List<ClientRepresentation> clients = keycloak.realm(realm).clients().findByClientId(authClientId);
        if (clients.isEmpty()) {
            throw new KeyCloakServiceException("No clients found for client Id: " + authClientId);
        }

        String _clientId = clients.get(0).getClientId();
        String _clientUid = clients.get(0).getId();
        RoleRepresentation clientRole = null;

        try{
            clientRole = keycloak.realm(realm).clients().get(_clientUid).roles().get(role.getRoleType().toString()).toRepresentation();
        } catch (jakarta.ws.rs.NotFoundException e) {
            throw new KeyCloakServiceException("Role '" + role.getRoleType().toString() + "' was not found in client: " + _clientId);
        }

        try {
            keycloak.realm(realm).users().get(userId).roles().clientLevel(_clientUid).add(Collections.singletonList(clientRole));
        }
        catch (Exception e) {
            throw new KeyCloakServiceException(e.getMessage());
        }
    }

    public void updateUserDetails(UUID customerUID, String firstName, String lastName, Role role) throws KeyCloakServiceException {
        UserResource userResource = null;
        try {
            userResource = keycloak.realm(realm).users().get(customerUID.toString());
        } catch (NotFoundException e) {
            throw new KeyCloakServiceException("No user found with UID: "+customerUID.toString());
        }
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);
        userResource.update(userRepresentation);

        ClientRepresentation clientRepresentation = keycloak.realm(realm).clients().findByClientId(authClientId).get(0);
        String _clientUid = clientRepresentation.getId();
        String _clientId = clientRepresentation.getClientId();
        // get all client roles assigned to the user for current client
        List<RoleRepresentation> roleRepresentations = userResource.roles().clientLevel(_clientUid).listAll();
        // remove all the roles
        userResource.roles().clientLevel(_clientUid).remove(roleRepresentations);
        RoleRepresentation newRole = null;
        // get the new role from client
        try{
            newRole = keycloak.realm(realm).clients().get(_clientUid).roles().get(role.getRoleType().toString()).toRepresentation();
        } catch (jakarta.ws.rs.NotFoundException e) {
            throw new KeyCloakServiceException("Role '" + role.getRoleType().toString() + "' was not found in client: " + _clientId);
        }

        // assign the new role
        try {
            userResource.roles().clientLevel(_clientUid).add(Collections.singletonList(newRole));
        }
        catch (Exception e) {
            throw new KeyCloakServiceException(e.getMessage());
        }
    }

    public void deleteUser(UUID customerUID) throws KeyCloakServiceException {
        UserResource userResource = null;
        try {
            userResource = keycloak.realm(realm).users().get(customerUID.toString());
        } catch (NotFoundException e) {
            throw new KeyCloakServiceException("No user found with UID: "+customerUID.toString());
        }
        userResource.remove();
    }
}
