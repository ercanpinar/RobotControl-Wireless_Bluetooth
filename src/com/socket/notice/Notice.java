package com.socket.notice;

import android.content.Context;
import android.widget.Toast;

public class Notice {

	private Toast toast;
	private Context context;

	public Notice(Context context){
		this.context = context;
	}
	
	public void showToast(String mensagem) {
		if (toast != null) {
			toast.setText(mensagem);
		} else {
			toast = Toast.makeText(context, mensagem, Toast.LENGTH_LONG);
		}
		toast.show();
	}

	public void closeToast() {
		if (toast != null)
			toast.cancel();
	}

}