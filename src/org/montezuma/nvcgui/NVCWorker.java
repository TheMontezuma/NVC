package org.montezuma.nvcgui;

import javax.swing.*;

public class NVCWorker extends SwingWorker<Void, Void> {
	
	private NVCMain nvc;
	private NVCGui gui;
	
	public NVCWorker(NVCMain n, NVCGui g)
	{
		nvc = n;
		gui = g;
	}
	
	public void setWorkerProgress(int p)
	{
		this.setProgress(p);
	}
	
	public boolean isWorkerCancelled()
	{
		return this.isCancelled();
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		nvc.restructure(this);
		return null;
	}
	
	protected void done()
	{
		gui.onJobFinished();
	}
	
}
