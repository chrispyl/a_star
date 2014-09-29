import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;

public class stateButton extends JButton implements MouseListener{

private static final long serialVersionUID = 1L;
boolean state=false;
	
	public stateButton(String string) {
		super(string);
		addMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(Grid.doingSomethingElse==false) state=true;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
