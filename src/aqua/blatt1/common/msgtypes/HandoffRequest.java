package aqua.blatt1.common.msgtypes;

import java.io.Serializable;

import aqua.blatt1.common.FishModel;

@SuppressWarnings("serial")
public record HandoffRequest(FishModel fish) implements Serializable {

}
