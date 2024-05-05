package aqua.blatt1.common.msgtypes;

import java.io.Serializable;
import java.net.InetSocketAddress;

@SuppressWarnings("serial")
public record NeighborUpdate(InetSocketAddress leftNeighbor, InetSocketAddress rightNeighbor) implements Serializable {
}