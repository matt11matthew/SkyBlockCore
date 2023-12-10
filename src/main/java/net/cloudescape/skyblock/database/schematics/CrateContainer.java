package net.cloudescape.skyblock.database.schematics;

import com.cloudescape.database.container.Container;
import com.cloudescape.database.wrappers.Wrapper;
import org.bson.Document;

public class CrateContainer extends Container {

    /**
     * Construct a new instance of {@link CrateContainer}.
     *
     * @param wrapper  - {@link SchematicWrapper}
     * @param document - Document.
     */
    public CrateContainer(Wrapper wrapper, Document document) {
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