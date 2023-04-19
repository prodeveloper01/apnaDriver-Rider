package com.qboxus.gograbdriver.mapclasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.qboxus.gograbdriver.helpingclasses.Functions;
import com.qboxus.gograbdriver.helpingclasses.Preferences;
import com.qboxus.gograbdriver.R;

import java.util.ArrayList;
import java.util.List;

public class MapWorker {

    final long DURATION_MS = 6000;
    public Bitmap currentLocationMarker, destinationMarker, carMarker, pickupMarkerBitmap;
    GoogleMap googleMap;
    Context context;
    Preferences preferences;
    Runnable runnable;
    Handler handler;
    boolean isMarkerRotating = false;


    public MapWorker(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        preferences = new Preferences(context);
        carMarker = getDriverPickUpView();
        destinationMarker = getMarkerDropOffPinView();
        pickupMarkerBitmap = getMarkerPickupPinView();
    }

    private Bitmap getDriverPickUpView() {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_my_car_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(R.drawable.ic_driver_car);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap getMarkerPickupPinView() {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_pickup_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    private Bitmap getMarkerDropOffPinView() {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_dropoff_marker, null);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }


    public Marker addMarker(LatLng latLng, Bitmap markerImage) {
        Marker m = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(markerImage)));
        return m;
    }

    public void animateMarkerWithMap(final Marker marker, final double latitude, final double longitude) {

        if (handler != null && runnable != null)
            handler.removeCallbacks(runnable);

        handler = new Handler();
        final LatLngInterpolator latLngInterpol = new LatLngInterpolator.LinearFixed();
        final Interpolator interpolator = new LinearInterpolator();

        final LatLng startPosition = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        final LatLng endPosition = new LatLng(latitude, longitude);
        final long start = SystemClock.uptimeMillis();

        runnable = new Runnable() {
            @Override
            public void run() {
                float elapsed = SystemClock.uptimeMillis() - start;
                float t = elapsed / DURATION_MS;
                float v = interpolator.getInterpolation(t);
                LatLng latLng = latLngInterpol.interpolate(v, startPosition, endPosition);
                marker.setPosition(latLng);
                if (t < 1)
                    handler.postDelayed(this, 16);
            }
        };
        handler.post(runnable);
    }

    public void rotateMarker(final Marker marker, final float toRotation) {
        if (!isMarkerRotating) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final float startRotation = marker.getRotation();

            final Interpolator interpolator = new LinearInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    isMarkerRotating = true;

                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / (DURATION_MS / 2));

                    float rot = t * toRotation + (1 - t) * startRotation;
                    marker.setAnchor(0.5f, 0.5f);
                    marker.setRotation(-rot > 180 ? rot / 2 : rot);
                    if (t < 1.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 46);
                    } else {
                        isMarkerRotating = false;
                    }
                }
            });
        }
    }

    public void drawRoute(Context context, LatLng pickup, LatLng dropoff, GoogleMap mMap) {
        MapsInitializer.initialize(context);
        GoogleDirection.withServerKey(context.getString(R.string.google_map_key))
                .from(pickup)
                .to(dropoff)
                .transportMode(TransportMode.DRIVING)
                .optimizeWaypoints(true)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        ArrayList<LatLng> directionPositionList = new ArrayList<>();
                        for (int i = 0; i < direction.getRouteList().size(); i++) {
                            Route route = direction.getRouteList().get(i);
                            directionPositionList = route.getLegList().get(0).getDirectionPoint();
                        }
                        if (directionPositionList.size() > 0) {
                            MapAnimator.getInstance().animateRoute(mMap, directionPositionList, true);
                        } else {
                            List<LatLng> polyLineList = new ArrayList<>();
                            polyLineList.add(pickup);
                            polyLineList.add(dropoff);
                            MapAnimator.getInstance().animateRoute(mMap, polyLineList, true);
                        }
                        Functions.logDMsg( rawBody);
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        List<LatLng> polyLineList = new ArrayList<>();
                        polyLineList.add(pickup);
                        polyLineList.add(dropoff);
                        MapAnimator.getInstance().animateRoute(mMap, polyLineList, true);
                    }
                });
    }

}