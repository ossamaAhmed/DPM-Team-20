package objectSearchRoutine;
import sensorController.*;

public class ObjectIdentifier {
	private FilteredUltrasonicPoller usPoller;
	private FilteredColorPoller colorPoller;
	
	public ObjectIdentifier(FilteredUltrasonicPoller usPoller, FilteredColorPoller colorPoller){
		this.usPoller = usPoller;
		this.colorPoller = colorPoller;
	}
	
	

}
