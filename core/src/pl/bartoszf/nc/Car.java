package pl.bartoszf.nc;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kebab on 23.10.2016.
 */
public class Car {
    Body body;
    List<Tire> tires = new ArrayList<Tire>();
    RevoluteJoint flJoint, frJoint;

    public Car(World world)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        Vector2[] vertices = new Vector2[8];
        for(int i=0;i<8;i++)
        {
            vertices[i] = new Vector2();
        }
        vertices[0].set( 1.5f,   0);
        vertices[1].set(   3, 2.5f);
        vertices[2].set( 2.8f, 5.5f);
        vertices[3].set(   1,  10);
        vertices[4].set(  -1,  10);
        vertices[5].set(-2.8f, 5.5f);
        vertices[6].set(  -3, 2.5f);
        vertices[7].set(-1.5f,   0);


        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set(vertices);
        body.createFixture(polygonShape, 0.1f);//shape, density
        body.setUserData(this);

        float maxForwardSpeed = 1800;
        float maxBackwardSpeed = -40;
        float backTireMaxDriveForce = 1200;
        float frontTireMaxDriveForce = 800;
        float backTireMaxLateralImpulse = 8.5f;
        float frontTireMaxLateralImpulse = 7.5f;

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = body;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.localAnchorB.setZero();

        Tire tire = new Tire(world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set( -3, 0.75f );
        world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set( 3, 0.75f );
        world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set( -3, 8.5f );
        flJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);

        tire = new Tire(world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.body;
        jointDef.localAnchorA.set( 3, 8.5f );
        frJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);
    }

    public void update(float drive, float angle)
    {
        for(Tire t: tires)
        {
            t.updateFriction();
            t.updateDrive(drive);
        }

        //tires.get(0).updateDrive(drive);
        //tires.get(1).updateDrive(drive);
        updateTurn(-angle);
    }

    void updateTurn(float angle)
    {
        float lockAngle = 35 * MathUtils.degreesToRadians;
        float turnSpeedPerSec = 320 * MathUtils.degreesToRadians;
        float turnPerTimeStep = turnSpeedPerSec / 60.0f;
        float desiredAngle = angle * lockAngle;

        float angleNow = flJoint.getJointAngle();
        float angleToTurn = desiredAngle - angleNow;
        angleToTurn = MathUtils.clamp(angleToTurn, -turnPerTimeStep, turnPerTimeStep);
        float newAngle = angleNow + angleToTurn;
        flJoint.setLimits(newAngle, newAngle);
        frJoint.setLimits(newAngle, newAngle);
        /*

         */
    }
}
