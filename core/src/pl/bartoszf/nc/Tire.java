package pl.bartoszf.nc;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Kebab on 21.10.2016.
 */
public class Tire {
    Body body;

    float m_maxForwardSpeed = 800;  // 100;
    float m_maxBackwardSpeed = -20; // -20;
    float m_maxDriveForce = 500;    // 150;
    float m_maxAngle = 35;
    float traction = 1f;
    float currentDrag = 1;
    private float m_maxLateralImpulse = 2.8f;
    private float m_lastDriveImpulse;
    private float m_lastLateralFrictionImpulse;

    public Tire(World world)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox( 0.5f, 1.25f );
        body.createFixture(polygonShape, 0.5f);//shape, density
        body.setUserData(this);
    }

    Vector2 getLateralVelocity() {
        Vector2 currentRightNormal = new Vector2(body.getWorldVector(new Vector2(1,0)));
        return currentRightNormal.scl(currentRightNormal.dot(body.getLinearVelocity()));
    }

    Vector2 getForwardVelocity() {
        Vector2 currentForwardNormal = new Vector2(body.getWorldVector(new Vector2(0,1)));
        return currentForwardNormal.scl(currentForwardNormal.dot(body.getLinearVelocity()));
    }

    void setCharacteristics(float maxForwardSpeed, float maxBackwardSpeed, float maxDriveForce, float maxLateralImpulse) {
        m_maxForwardSpeed = maxForwardSpeed;
        m_maxBackwardSpeed = maxBackwardSpeed;
        m_maxDriveForce = maxDriveForce;
        m_maxLateralImpulse = maxLateralImpulse;
    }

    public void updateFriction() {
        body.applyAngularImpulse(traction * 0.1f * body.getInertia() * -body.getAngularVelocity(), true);

        Vector2 currentForwardNormal = new Vector2(getForwardVelocity());
        float currentForwardSpeed = currentForwardNormal.len();
        float dragForceMagnitude = -0.25f * currentForwardSpeed;
        dragForceMagnitude *= currentDrag;

        body.applyForce(currentForwardNormal.scl(traction * dragForceMagnitude),body.getWorldCenter(),true);
    }

    public void updateDrive(float f)
    {
        float desiredSpeed = 0;
        if(f > 0)
        {
            desiredSpeed = m_maxForwardSpeed;
        }
        else if(f < 0)
        {
            desiredSpeed = m_maxBackwardSpeed;
        }

        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0,1));
        float currentSpeed = currentForwardNormal.dot(getForwardVelocity());


        float force = 0;
        if(f != 0)
        {
            if ( desiredSpeed > currentSpeed )
                force = m_maxDriveForce;
            else if ( desiredSpeed < currentSpeed )
                force = -m_maxDriveForce * 0.5f;
        }

        float speedFactor = currentSpeed / 120;

        currentForwardNormal = body.getWorldVector(new Vector2(0,1));
        Vector2 driveImpulse =  currentForwardNormal.scl(force / 60.0f);
        currentForwardNormal = body.getWorldVector(new Vector2(0,1));
        if ( driveImpulse.len() > m_maxLateralImpulse )
            driveImpulse = driveImpulse.scl(m_maxLateralImpulse / driveImpulse.len());

        Vector2 latVel = getLateralVelocity();
        Vector2 lateralFrictionImpulse = latVel.scl(-body.getMass());
        float lateralImpulseAvailable = m_maxLateralImpulse;
        lateralImpulseAvailable *= 2.0f * speedFactor;
        if ( lateralImpulseAvailable < 0.5f * m_maxLateralImpulse )
            lateralImpulseAvailable = 0.5f * m_maxLateralImpulse;

        if(lateralFrictionImpulse.len() > lateralImpulseAvailable)
        {
            lateralFrictionImpulse = lateralFrictionImpulse.scl(lateralImpulseAvailable / lateralFrictionImpulse.len());
        }

        m_lastDriveImpulse = driveImpulse.len();
        m_lastLateralFrictionImpulse = lateralFrictionImpulse.len();

        Vector2 impulse = driveImpulse.add(lateralFrictionImpulse);
        if(impulse.len() > m_maxLateralImpulse)
        {
            impulse = impulse.scl(m_maxLateralImpulse / impulse.len());
        }

        body.applyLinearImpulse(impulse.scl(traction), body.getWorldCenter(), true);
    }

    public void updateTurn(float f)
    {
        float desiredTorque = -f *m_maxAngle;

        body.applyTorque(desiredTorque,true);
    }

    /*
      void updateTurn(int controlState) {
      float desiredTorque = 0;
      switch ( controlState & (TDC_LEFT|TDC_RIGHT) ) {
          case TDC_LEFT:  desiredTorque = 15;  break;
          case TDC_RIGHT: desiredTorque = -15; break;
          default: ;//nothing
      }
      m_body->ApplyTorque( desiredTorque );
  }
    */
}
