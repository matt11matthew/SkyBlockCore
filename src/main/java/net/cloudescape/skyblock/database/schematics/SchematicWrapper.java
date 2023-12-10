package net.cloudescape.skyblock.database.schematics;

import com.cloudescape.database.wrappers.Wrapper;
import com.mongodb.client.model.Filters;
import net.cloudescape.skyblock.schematics.Schematic;
import org.bson.Document;

public class SchematicWrapper extends Wrapper {

    public static final String COLLECTION_NAME = "schematics";

    public SchematicWrapper() {
    }

    /**
     * Add a new schematic to the MongoDB database.
     *
     * @param schematic - schematic.
     * @return Document
     */
    public Document addSchematic(Schematic schematic) {
        Document doc = this.createDocument("uniqueID", schematic.getName());
//        TODO
//        for (SchematicBlock block : schematic.getBlocks()) {
//            doc = this.setValue(doc, "blocks." + block.getLocation() + ".id", block.getId());
//            doc = this.setValue(doc, "blocks." + block.getLocation() + ".data", block.getData());
//
//            Material material = Material.getMaterial(block.getId());
//
//            if (block.getContents() != null) {
//                for (ItemStack item : block.getContents()) {
//                    doc = this.setValue(doc, "blocks." + block.getLocation() + ".contents." + item.getSlot(), item.getFormattedItem())
//                }
//            }
//        }
        this.updateDocument(doc);
        return doc;
    }

    /**
     * Remove a schematic from the database.
     *
     * @param schematic - schematic.
     */
    public void removeSchematic(Schematic schematic) {
        this.getCollection().deleteOne(Filters.eq("uniqueID", schematic.getName()));
    }

    /**
     * Get a schematic from MongoDB by its name.
     *
     * @param schematicName - schematics name.
     * @return SchematicContainer
     */
    public SchematicContainer getSchematicByName(String schematicName) {
        Document document = search("uniqueID", schematicName);

        if (document == null) return null;

        return new SchematicContainer(this, document);
    }

}
