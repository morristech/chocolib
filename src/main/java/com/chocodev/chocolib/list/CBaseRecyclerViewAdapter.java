package com.chocodev.chocolib.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by DRM on 19/09/13.
 */
public class CBaseRecyclerViewAdapter<T, Q extends BindableView<T>> extends RecyclerView.Adapter {

    public static final String TAG = CBaseRecyclerViewAdapter.class.getName();

    public static class ViewHolder<Q extends BindableView> extends RecyclerView.ViewHolder {
        Q v;
        public ViewHolder(Q v) {
            super(v);
            this.v=v;
        }

        public void bind(Object t, int size, int position) {
            v.bind(t,size,position);
        }
    }

    private Class viewClass;
    private Class objectClass;
    private List<T> items;
    private ListEventListener listEventListener;
    private Method builderMethod = null;

    public CBaseRecyclerViewAdapter(Class<T> objectClass, Class<Q> viewClass, List<T> items) {
        this.objectClass = objectClass;
        this.viewClass = viewClass;
        this.items = items;
        try {
            builderMethod = viewClass.getMethod("build", Context.class);
        } catch (Exception ex) {

        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder vh = new ViewHolder<>(getView(parent));
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder<Q>)holder).bind(items.get(position), items.size(), position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public BindableView<T> getView(ViewGroup parent) {
        BindableView<T> viewGroup=null;

            if (builderMethod == null) {
                // has no build
                try {
                    Constructor constructor = viewClass.getConstructor(Context.class);
                    viewGroup = (BindableView<T>) constructor.newInstance(parent.getContext());
                } catch (InstantiationException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (InvocationTargetException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (NoSuchMethodException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } else {
                try {
                    viewGroup = (BindableView<T>) builderMethod.invoke(null, new Object[]{parent.getContext()});
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage(), ex);
                    return null;
                }
            }
        if (listEventListener != null) {
            viewGroup.setListEventListener(listEventListener);
        }
        return viewGroup;
    }

    public void setListEventListener(ListEventListener listEventListener) {
        this.listEventListener = listEventListener;
    }

    public void notifyAction(int actionId, T object, View view) {
        if (listEventListener != null) {
            listEventListener.onListEvent(actionId, object, view);
        }
    }
}