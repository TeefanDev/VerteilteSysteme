package aqua.blatt1.common.msgtypes;

import java.io.Serializable;
import java.net.InetSocketAddress;

@SuppressWarnings("serial")
public class NeighborUpdate implements Serializable {
	private InetSocketAddress adress;
	private InetSocketAddress adressLeft;
	private InetSocketAddress adressRight;
	
	public NeighborUpdate(InetSocketAddress adress) {
		this.adress = adress;
	}
	
	public InetSocketAddress getRightNeighbor() {
		return adressRight;		
	}
	
	public InetSocketAddress getLeftNeighbor() {
		return adressLeft;		
	}

}
