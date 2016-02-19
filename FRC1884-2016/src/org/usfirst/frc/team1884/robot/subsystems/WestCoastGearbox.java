package org.usfirst.frc.team1884.robot.subsystems;

import org.usfirst.frc.team1884.robot.NEXUS;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;

public class WestCoastGearbox {
	private static final int LEFT_CHANNEL = 0;
	private static final int RIGHT_CHANNEL = 1;

	private DoubleSolenoid gearShiftPush, ptoPush;
	private Joystick joystick;
	private RobotDrive drive;
	private VictorSP leftSide, rightSide;

	private long timeOfLastExtensionPTO = 0;

	private long lastPTOButtonExtend = 0;
	private long lastPTOButtonRetract = 0;

	public static WestCoastGearbox INSTANCE;

	private boolean isInverted = false;
	private boolean isArcadeDrive = true;

	static {
		INSTANCE = new WestCoastGearbox();
	}

	private WestCoastGearbox() {
		gearShiftPush = new DoubleSolenoid(3, 2);
		ptoPush = new DoubleSolenoid(1, 0);

		joystick = NEXUS.DRIVESTICK;

		leftSide = new VictorSP(LEFT_CHANNEL);
		rightSide = new VictorSP(RIGHT_CHANNEL);

		drive = new RobotDrive(leftSide, rightSide);
		drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
		drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
	}

	public void teleopInit() {
		// TODO (probably nothing)
	}

	public void teleopPeriodic() {
		teleopDrive();
		PTOShift();
		reverse();
	}

	public void setMotorSpeed(double leftSpeed, double rightSpeed) {
		leftSide.set(leftSpeed);
		rightSide.set(rightSpeed);
	}

	public void reverse() {
		if (joystick.getRawButton(6)) {
			isInverted = !isInverted;
			drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, !isInverted);
			drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, !isInverted);
		}
	}

	public void PTOShift() {
		// RIP Gear Shift, our dearly beloved, which Mr. Ali would like to
		// always be extending in order to not rek our robot
		gearShiftPush.set(DoubleSolenoid.Value.kForward);

		//Luckily, the PTO is working fine
		if (joystick.getRawButton(2) && ptoPush.get() == DoubleSolenoid.Value.kOff
				&& System.currentTimeMillis() - lastPTOButtonExtend > 200) {
			ptoPush.set(DoubleSolenoid.Value.kForward);
			lastPTOButtonExtend = System.currentTimeMillis();
			timeOfLastExtensionPTO = System.currentTimeMillis();
		}
		if (joystick.getRawButton(4) && ptoPush.get() == DoubleSolenoid.Value.kOff
				&& System.currentTimeMillis() - lastPTOButtonRetract > 200) {
			ptoPush.set(DoubleSolenoid.Value.kReverse);
			lastPTOButtonRetract = System.currentTimeMillis();
			timeOfLastExtensionPTO = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() - timeOfLastExtensionPTO > 1000) {
			ptoPush.set(DoubleSolenoid.Value.kOff);
			timeOfLastExtensionPTO = Long.MAX_VALUE;
		}
	}

	public void teleopDrive() {
		if (isArcadeDrive) {
			drive.arcadeDrive(joystick);
		} else {
			drive.tankDrive(joystick, 1, joystick, 5);
		}

		if (joystick.getRawButton(6)) {
			isArcadeDrive = !isArcadeDrive;
		}
	}
}
