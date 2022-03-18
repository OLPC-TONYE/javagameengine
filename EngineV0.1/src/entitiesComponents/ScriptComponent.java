package entitiesComponents;

import java.io.File;

import scripting.EntityScript;

public class ScriptComponent extends Component
{

	private transient EntityScript script;	
	private String script_path;
	
	@Override
	public void prepare() {
		
	}

	@Override
	public void update(double dt) {
		if(script != null) {
			script.update(dt);
		}
	}
	
	public void onCreate() {
		if(script != null) {
			script.onCreate();	
		}
	}

	public void onDestroy() {
		if(script != null) {
			script.onDestroy();;	
		}
	}
	
	public <C extends EntityScript> void bind() {

	}
	
	public void bind(EntityScript script) {
		script.bindToEntity(entity);
		this.script = script;
	}
	
	public boolean validate(String path) {
		if(!script_path.isEmpty()) {
			File script_file = new File(script_path);
			return script_file.exists();
		}
		return false;
	}
}
