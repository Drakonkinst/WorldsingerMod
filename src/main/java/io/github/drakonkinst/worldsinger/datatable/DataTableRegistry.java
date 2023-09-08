package io.github.drakonkinst.worldsinger.datatable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.drakonkinst.worldsinger.util.ModConstants;
import io.github.drakonkinst.worldsinger.util.json.JsonStack;
import io.github.drakonkinst.worldsinger.util.json.JsonType;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.StringUtils;

public class DataTableRegistry extends JsonDataLoader implements
        IdentifiableResourceReloadListener {

    public static DataTableRegistry INSTANCE;

    private static final DataTable DUMMY = new DataTable(DataTableType.MISC, 0,
            new Object2IntArrayMap<>(), null);
    private static final Identifier IDENTIFIER = new Identifier(ModConstants.MOD_ID, "data_tables");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DataTableType[] DATA_TABLE_TYPES = DataTableType.values();

    private final Map<Identifier, DataTable> dataTables = new HashMap<>();
    private boolean tagsResolved = false;

    public DataTableRegistry() {
        super(GSON, "data_tables");
        INSTANCE = this;
    }

    public DataTable get(Identifier id) {
        if (!tagsResolved) {
            ModConstants.LOGGER.warn("Attempting to access data table " + id
                    + " before tags are fully resolved, results may be inaccurate");
        }
        return dataTables.getOrDefault(id, DUMMY);
    }

    public Optional<DataTable> getOptional(Identifier id) {
        if (!tagsResolved) {
            ModConstants.LOGGER.warn("Attempting to access data table " + id
                    + " before tags are fully resolved, results may be inaccurate");
        }
        DataTable dataTable = dataTables.get(id);
        return dataTable == null ? Optional.empty() : Optional.of(dataTable);
    }

    public boolean contains(Identifier id) {
        if (!tagsResolved) {
            ModConstants.LOGGER.warn("Attempting to access data table " + id
                    + " before tags are fully resolved, results may be inaccurate");
        }
        return dataTables.containsKey(id);
    }

    public Collection<Identifier> getDataTableIds() {
        return dataTables.keySet();
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> data, ResourceManager manager,
            Profiler profiler) {
        tagsResolved = false;
        dataTables.clear();
        data.forEach(this::loadDataTable);
        ModConstants.LOGGER.info("Loaded " + dataTables.size() + " data tables");
    }

    private void loadDataTable(Identifier dataTableId, JsonElement element) {
        JsonStack jsonStack = new JsonStack(GSON, element);
        jsonStack.allow("replace", "type", "default", "entries");

        boolean replace = jsonStack.getBooleanOrElse("replace", false);
        int defaultValue = jsonStack.maybeInt("default").orElse(0);
        Optional<String> typeStr = jsonStack.maybeString("type");
        DataTableType type = typeStr.map(str -> getMatchingType(jsonStack, str.toLowerCase()))
                .orElse(DataTableType.MISC);

        Object2IntMap<Identifier> entryTable = new Object2IntArrayMap<>();
        Object2IntMap<Identifier> unresolvedTags = null;
        jsonStack.push("entries");
        for (Map.Entry<String, JsonElement> entry : jsonStack.peek().entrySet()) {
            String key = entry.getKey();
            JsonElement valueEl = entry.getValue();
            int value;
            if (JsonType.NUMBER.is(valueEl)) {
                value = JsonType.NUMBER.cast(valueEl).getAsInt();
            } else {
                jsonStack.addError("Expected data table entry value to be an integer");
                continue;
            }

            if (key.startsWith("#")) {
                if (unresolvedTags == null) {
                    unresolvedTags = new Object2IntArrayMap<>();
                }
                String tag = key.substring(1);
                unresolvedTags.put(new Identifier(tag), value);
            } else {
                entryTable.put(new Identifier(key), value);
            }
        }

        List<String> errors = jsonStack.getErrors();
        if (!errors.isEmpty()) {
            ModConstants.LOGGER.error(
                    "Failed to parse data table " + dataTableId + ": " + StringUtils.join(errors));
            return;
        }

        if (dataTables.containsKey(dataTableId) && !replace) {
            DataTable existingDataTable = dataTables.get(dataTableId);
            if (existingDataTable.getType() != type) {
                ModConstants.LOGGER.warn(
                        "Tried to override data table but data table types do not match: expected "
                                + existingDataTable.getType().getName() + ", got "
                                + type.getName());
                return;
            }
            existingDataTable.merge(entryTable, unresolvedTags);
        } else {
            dataTables.put(dataTableId,
                    new DataTable(type, defaultValue, entryTable, unresolvedTags));
        }
    }

    private DataTableType getMatchingType(JsonStack jsonStack, String typeStr) {
        for (DataTableType type : DATA_TABLE_TYPES) {
            if (type.getName().equals(typeStr)) {
                return type;
            }
        }
        jsonStack.addError("Unrecognized data table type " + typeStr);
        return DataTableType.MISC;
    }

    public void resolveTags() {
        int numResolvedTables = 0;
        for (Map.Entry<Identifier, DataTable> entry : dataTables.entrySet()) {
            DataTable dataTable = entry.getValue();
            List<Identifier> failedTags = dataTable.resolveTags();
            if (failedTags != null && !failedTags.isEmpty()) {
                if (dataTable.getType() == DataTableType.MISC) {
                    ModConstants.LOGGER.warn(
                            "Data table " + entry.getKey() + " is of type " + dataTable.getType()
                                    + " and is unable to resolve tags. Specify a type to resolve.");
                } else {
                    ModConstants.LOGGER.error(
                            "Failed to resolve tags for data table " + entry.getKey()
                                    + ": Unrecognized tags " + StringUtils.join(
                                    failedTags.stream().map(Identifier::toString).toList()));
                }
            } else {
                numResolvedTables++;
            }
        }
        ModConstants.LOGGER.info("Resolved tags for " + numResolvedTables + " data tables");
        tagsResolved = true;
    }

    @Override
    public Identifier getFabricId() {
        return IDENTIFIER;
    }
}
