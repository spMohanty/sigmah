package org.sigmah.offline.indexeddb;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.offline.event.JavaScriptEvent;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class NativeOpenDatabaseRequest extends Request implements OpenDatabaseRequest {
	private final List<JavaScriptEvent> upgradeNeededHandlers = new ArrayList<JavaScriptEvent>();
	private final List<JavaScriptEvent> blockedHandlers = new ArrayList<JavaScriptEvent>();

	NativeOpenDatabaseRequest(IDBOpenDBRequest request) {
		super(request);
		registerEvents(request);
	}
	
	private native void registerEvents(IDBRequest request) /*-{
		if(typeof $wnd.Object.getPrototypeOf != 'undefined') {
			$wnd.Object.getPrototypeOf(this).handleEvent = function(event) {
				switch(event.type) {
					case 'success':
						this.@org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest::fireSuccess(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					case 'error':
						this.@org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest::fireError(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					case 'upgradeneeded':
						this.@org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest::fireUpgradeNeeded(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					case 'blocked':
						this.@org.sigmah.offline.indexeddb.NativeOpenDatabaseRequest::fireBlocked(Lcom/google/gwt/core/client/JavaScriptObject;)(event);
						break;
					default:
						break;
				}
			};
			request.addEventListener('upgradeneeded', this);
			request.addEventListener('blocked', this);
		}
	}-*/;

	@Override
	public Database getResult() {
		return new Database((IDBDatabase)super.getResult());
	}
    
    @Override
    public void addSuccessHandler(final JavaScriptEvent handler) {
        addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while opening an IndexedDB database.", caught);
            }

            @Override
            public void onSuccess(Request result) {
                handler.onEvent(null);
            }
        });
    }
	
	public void addUpgradeNeededHandler(JavaScriptEvent<IDBVersionChangeEvent> handler) {
		upgradeNeededHandlers.add(handler);
	}
	
	protected void fireUpgradeNeeded(JavaScriptObject event) {
		for(int index = upgradeNeededHandlers.size() - 1; index >= 0; index--) {
			upgradeNeededHandlers.get(index).onEvent(event);
		}
	}
	
	public void addBlockedHandler(JavaScriptEvent handler) {
		blockedHandlers.add(handler);
	}
	
	protected void fireBlocked(JavaScriptObject event) {
		for(int index = blockedHandlers.size() - 1; index >= 0; index--) {
			blockedHandlers.get(index).onEvent(event);
		}
	}
}
