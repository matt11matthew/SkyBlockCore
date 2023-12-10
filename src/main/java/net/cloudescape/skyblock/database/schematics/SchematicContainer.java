package net.cloudescape.skyblock.database.schematics;

import com.cloudescape.database.container.Container;
import com.cloudescape.database.wrappers.Wrapper;
import net.cloudescape.skyblock.database.island.IslandWrapper;
import net.cloudescape.skyblock.island.temple.BoosterTemple;
import org.bson.Document;

import java.util.UUID;

public class SchematicContainer extends Container {

    /**
     * Construct a new instance of {@link SchematicContainer}.
     *
     * @param wrapper  - {@link SchematicWrapper}
     * @param document - Document.
     */
    public SchematicContainer(Wrapper wrapper, Document document) {
        super(wrapper, document);
    }

    /**
     * @return Get the Schematics name.
     */
    public String getSchematicName() {
        return ((String) this.getValue("uniqueID", null));
    }

    /**
     * Set the schematics name.
     * @param name - schematic name.
     */
    public void setSchematicName(String name) { this.setValue("uniqueID", name); }
}