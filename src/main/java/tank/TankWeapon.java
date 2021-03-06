package tank;

import wingman.game.Bullet;
import wingman.game.Ship;
import wingman.modifiers.weapons.AbstractWeapon;

import java.awt.*;

public class TankWeapon extends AbstractWeapon {
    public TankWeapon(TankWorld tankWorld) {
        super(tankWorld);
    }

    public void fireWeapon(Ship theTank,TankWorld tankWorld){
        super.fireWeapon(theTank);
        Point location = theTank.getLocationPoint();
        Point offset = theTank.getGunLocation();
        location.x += offset.x;
        location.y += offset.y;
        Point speed = new Point(0, -15 * direction);
        int strength = 10;
        reload = 15;

        TankBullet bullet = new TankBullet(location, speed, strength, (Tank) theTank,tankWorld);
        bullets = new Bullet[1];
        bullets[0] = bullet;
        this.setChanged();
        this.notifyObservers();
    }
}