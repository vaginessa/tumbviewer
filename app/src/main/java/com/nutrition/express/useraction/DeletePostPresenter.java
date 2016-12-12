package com.nutrition.express.useraction;

import com.nutrition.express.model.rest.ApiService.BlogService;
import com.nutrition.express.model.rest.ResponseListener;
import com.nutrition.express.model.rest.RestCallback;
import com.nutrition.express.model.rest.RestClient;
import com.nutrition.express.model.rest.bean.BaseBean;

import retrofit2.Call;

/**
 * Created by huang on 12/12/16.
 */

public class DeletePostPresenter implements DeletePostContract.Presenter, ResponseListener {
    public static final String ACTION_DELETE_POST = "DELETE_POST";
    private DeletePostContract.View view;
    private BlogService service;
    private Call<BaseBean<Void>> call;
    private int position;

    public DeletePostPresenter(DeletePostContract.View view) {
        this.view = view;
        service = RestClient.getInstance().getBlogService();
    }

    public void onAttach() {

    }

    @Override
    public void onDetach() {
        view = null;
    }

    @Override
    public void deletePost(String blogName, String postId, int position) {
        if (call == null) {
            this.position = position;
            call = service.deletePost(blogName, postId);
            call.enqueue(new RestCallback<Void>(this, null));
        }

    }

    @Override
    public void onResponse(BaseBean baseBean, String tag) {
        if (view == null) {
            return;
        }
        call = null;

        view.onDeletePostSuccess(position);
    }

    @Override
    public void onError(int code, String error, String tag) {
        if (view == null) {
            return;
        }
        call = null;

        view.onError(code, error);
    }

    @Override
    public void onFailure(Throwable t, String tag) {
        if (view == null) {
            return;
        }
        call = null;

        view.onFailure(t);
    }
}
