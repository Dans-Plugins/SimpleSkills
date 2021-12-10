package dansplugins.simpleskills.api.preponderous.ponder;

import dansplugins.simpleskills.api.preponderous.ponder.misc.PonderAPI_Integrator;
import dansplugins.simpleskills.api.preponderous.ponder.toolbox.Toolbox;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Daniel Stephenson
 */
public abstract class AbstractPonderPlugin extends JavaPlugin {

    protected PonderAPI_Integrator ponderAPI_integrator;
    protected Toolbox toolbox;

    public Ponder getPonderAPI() {
        return ponderAPI_integrator.getAPI();
    }

    public Toolbox getToolbox() {
        return toolbox;
    }

}
