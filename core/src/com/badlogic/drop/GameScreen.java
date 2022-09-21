package com.badlogic.drop;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;


public class GameScreen implements Screen {final Drop game;

    Texture chaubaImage;
    Texture glovsImage;
    Texture tankImage;
    Sound dropSound;
    TextureRegion backGround;
    Music shumMusik;
    Rectangle sirena;
    OrthographicCamera camera;
    long lastDropTime;
    Array<Rectangle> drugdrops;
    int dropsGathered = 0;
    Texture[] textures;

    public GameScreen(final Drop gam) {
        this.game = gam;
        backGround = new TextureRegion(new Texture("back.jpg"), 0, 0, 1286, 904);

        // загрузка изображений для капли и ведра, 64x64 пикселей каждый
        chaubaImage = new Texture(Gdx.files.internal("chayba.png"));
        tankImage = new Texture(Gdx.files.internal("tank.png"));
        textures = new Texture[]{chaubaImage, tankImage};
        glovsImage = new Texture(Gdx.files.internal("glovs.png"));

        // загрузка звукового эффекта падающей капли и фоновой "музыки" дождя
        dropSound = Gdx.audio.newSound(Gdx.files.internal("sirena.wav"));
        shumMusik = Gdx.audio.newMusic(Gdx.files.internal("shum.mp3"));
        shumMusik.setLooping(true);

        // создает камеру
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // создается Rectangle для представления ведра
        sirena = new Rectangle();
        // центрируем ведро по горизонтали
        sirena.x = 800 / 2 - 64 / 2;
        // размещаем на 20 пикселей выше нижней границы экрана.
        sirena.y = 20;

        sirena.width = 64;
        sirena.height = 64;

        // создает массив капель и возрождает первую
        drugdrops = new Array<Rectangle>();
        spawnRaindrop();

    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, 800 - 64);
        raindrop.y = 480;
        raindrop.width = 64;
        raindrop.height = 64;
        drugdrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // очищаем экран темно-синим цветом.
        // Аргументы для glClearColor красный, зеленый
        // синий и альфа компонент в диапазоне [0,1]
        // цвета используемого для очистки экрана.

        // сообщает камере, что нужно обновить матрицы.
        camera.update();

        // сообщаем SpriteBatch о системе координат
        // визуализации указанных для камеры.
        game.batch.setProjectionMatrix(camera.combined);

        // начитаем новую серию, рисуем ведро и
        // все капли
        game.batch.begin();
        game.batch.draw(backGround, 0,0);
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
        game.batch.draw(glovsImage, sirena.x, sirena.y);
        for (Rectangle raindrop : drugdrops) {
            game.batch.draw(chaubaImage, raindrop.x, raindrop.y);
        }
        game.batch.end();

        // обработка пользовательского ввода
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            sirena.x = touchPos.x - 64 / 2;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            sirena.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            sirena.x += 200 * Gdx.graphics.getDeltaTime();

        // убедитесь, что ведро остается в пределах экрана
        if (sirena.x < 0)
            sirena.x = 0;
        if (sirena.x > 800 - 64)
            sirena.x = 800 - 64;

        // проверка, нужно ли создавать новую каплю
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnRaindrop();

        // движение капли, удаляем все капли выходящие за границы экрана
        // или те, что попали в ведро. Воспроизведение звукового эффекта
        // при попадании.
        Iterator<Rectangle> iter = drugdrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if(dropsGathered >= 20){
                raindrop.y -= 250 * Gdx.graphics.getDeltaTime();
            }
            if(dropsGathered >= 40){
                raindrop.y -= 350 * Gdx.graphics.getDeltaTime();
            }
            if(dropsGathered >= 60){
                raindrop.y -= 500 * Gdx.graphics.getDeltaTime();
            }
            if(dropsGathered > 100){
                raindrop.y -= 800 * Gdx.graphics.getDeltaTime();
            }
            if (raindrop.y + 64 < 0)
                iter.remove();
            if (raindrop.overlaps(sirena)) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // воспроизведение фоновой музыки
        // когда отображается экрана
        shumMusik.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        chaubaImage.dispose();
        glovsImage.dispose();
        dropSound.dispose();
        shumMusik.dispose();
    }
    public static int rnd(int max)
    {
        return (int) (Math.random() * ++max);
    }
}
