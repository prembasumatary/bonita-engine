import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bonitasoft.engine.session.APISession;

public class AddressDAOImpl
    implements AddressDAO
{

    private APISession session;

    public AddressDAOImpl(APISession session) {
        this.session = session;
    }

    public List<Address> findByStreet(String street, int startIndex, int maxResults) {
        try {
            org.bonitasoft.engine.api.CommandAPI commandApi = com.bonitasoft.engine.api.TenantAPIAccessor.getCommandAPI(session);
            Map<String, Serializable> commandParameters = new HashMap<String, Serializable>();
            commandParameters.put("queryName", "Address.findByStreet");
            commandParameters.put("returnType", "Address");
            commandParameters.put("returnsList", true);
            commandParameters.put("startIndex", startIndex);
            commandParameters.put("maxResults", maxResults);
            Map<String, Serializable> queryParameters = new HashMap<String, Serializable>();
            queryParameters.put("street", street);
            commandParameters.put("queryParameters", ((Serializable) queryParameters));
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return ((List<Address> ) mapper.readValue(((byte[]) commandApi.execute("executeBDMQuery", commandParameters)), mapper.getTypeFactory().constructCollectionType(List.class, Address.class)));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Address> findByCity(String city, int startIndex, int maxResults) {
        try {
            org.bonitasoft.engine.api.CommandAPI commandApi = com.bonitasoft.engine.api.TenantAPIAccessor.getCommandAPI(session);
            Map<String, Serializable> commandParameters = new HashMap<String, Serializable>();
            commandParameters.put("queryName", "Address.findByCity");
            commandParameters.put("returnType", "Address");
            commandParameters.put("returnsList", true);
            commandParameters.put("startIndex", startIndex);
            commandParameters.put("maxResults", maxResults);
            Map<String, Serializable> queryParameters = new HashMap<String, Serializable>();
            queryParameters.put("city", city);
            commandParameters.put("queryParameters", ((Serializable) queryParameters));
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return ((List<Address> ) mapper.readValue(((byte[]) commandApi.execute("executeBDMQuery", commandParameters)), mapper.getTypeFactory().constructCollectionType(List.class, Address.class)));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Address> find(int startIndex, int maxResults) {
        try {
            org.bonitasoft.engine.api.CommandAPI commandApi = com.bonitasoft.engine.api.TenantAPIAccessor.getCommandAPI(session);
            Map<String, Serializable> commandParameters = new HashMap<String, Serializable>();
            commandParameters.put("queryName", "Address.find");
            commandParameters.put("returnType", "Address");
            commandParameters.put("returnsList", true);
            commandParameters.put("startIndex", startIndex);
            commandParameters.put("maxResults", maxResults);
            Map<String, Serializable> queryParameters = new HashMap<String, Serializable>();
            commandParameters.put("queryParameters", ((Serializable) queryParameters));
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return ((List<Address> ) mapper.readValue(((byte[]) commandApi.execute("executeBDMQuery", commandParameters)), mapper.getTypeFactory().constructCollectionType(List.class, Address.class)));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public List<Address> findAddressesByEmployeePersistenceId(Long persistenceId, int startIndex, int maxResults) {
        try {
            org.bonitasoft.engine.api.CommandAPI commandApi = com.bonitasoft.engine.api.TenantAPIAccessor.getCommandAPI(session);
            Map<String, Serializable> commandParameters = new HashMap<String, Serializable>();
            commandParameters.put("queryName", "Address.findAddressesByEmployeePersistenceId");
            commandParameters.put("returnType", "Address");
            commandParameters.put("returnsList", true);
            commandParameters.put("startIndex", startIndex);
            commandParameters.put("maxResults", maxResults);
            Map<String, Serializable> queryParameters = new HashMap<String, Serializable>();
            queryParameters.put("persistenceId", persistenceId);
            commandParameters.put("queryParameters", ((Serializable) queryParameters));
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return ((List<Address> ) mapper.readValue(((byte[]) commandApi.execute("executeBDMQuery", commandParameters)), mapper.getTypeFactory().constructCollectionType(List.class, Address.class)));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}