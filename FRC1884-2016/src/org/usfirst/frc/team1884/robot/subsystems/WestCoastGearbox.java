package org.usfirst.frc.team1884.robot.subsystems;

import org.usfirst.frc.team1884.robot.NEXUS;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.VictorSP;

public class WestCoastGearbox {
	private static final int LEFT_CHANNEL = 0;
	private static final int RIGHT_CHANNEL = 1;

	/** @deprecated */
	private DoubleSolenoid gearShiftPush, ptoPush;

	private Joystick joystick;
	private RobotDrive drive;
	private VictorSP leftSide, rightSide;

	/** @deprecated */
	private static final int GEAR_SHIFT_CHANNEL_EXTEND = 4;
	/** @deprecated */
	private static final int GEAR_SHIFT_CHANNEL_RETRACT = 2;
	/** @deprecated */
	private static final int PTO_CHANNEL_EXTEND = 3;
	/** @deprecated */
	private static final int PTO_CHANNEL_RETRACT = 5;

	/** @deprecated */
	private long timeOfLastExtensionPTO = 0;

	/** @deprecated */
	private long lastPTOButtonExtend = 0;
	/** @deprecated */
	private long lastPTOButtonRetract = 0;

	public static final WestCoastGearbox INSTANCE;

	private boolean isInverted = false;
	private boolean isTankDrive = false;

	private boolean joystickOneFirst = false;

	static {
		INSTANCE = new WestCoastGearbox();
	}

	private WestCoastGearbox() {

		// gearShiftPush = new DoubleSolenoid(GEAR_SHIFT_CHANNEL_EXTEND,
		// GEAR_SHIFT_CHANNEL_RETRACT);
		// ptoPush = new DoubleSolenoid(PTO_CHANNEL_EXTEND,
		// PTO_CHANNEL_RETRACT);

		joystick = NEXUS.DRIVESTICK;

		leftSide = new VictorSP(LEFT_CHANNEL);
		rightSide = new VictorSP(RIGHT_CHANNEL);

		// drive = new RobotDrive(leftSide, rightSide);
		// drive.setInvertedMotor(RobotDrive.MotorType.kRearLeft, true);
		// drive.setInvertedMotor(RobotDrive.MotorType.kRearRight, true);
		leftSide.setInverted(true);
		rightSide.setInverted(false);
	}

	public void teleopInit() {
		// TODO (probably nothing)
	}

	public void teleopPeriodic() {
		teleopDrive();
		// reverse();
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

	/** @deprecated */
	public void secureGearShift() {
		// RIP Gear Shift, our dearly beloved, which Mr. Ali would like to
		// always be extending in order to not rek our robot
		gearShiftPush.set(DoubleSolenoid.Value.kForward);
	}

	boolean toggleDrive;
	boolean toggleDriveLast;

	public void teleopDrive() {
		limitedSwerve();

		if (joystick.getRawButton(5) && !toggleDrive && !toggleDriveLast) {
			isTankDrive = !isTankDrive;
			toggleDrive = true;
		} else {
			toggleDrive = false;
		}
		toggleDriveLast = toggleDrive;
	}

	private void limitedSwerve() {
		double y = joystick.getRawAxis(4);
		double x = joystick.getRawAxis(1);

		x *= Math.abs(x) * 1.08;
		y *= Math.abs(y) * 1.08;

		leftSide.set(-y + x);
		rightSide.set(y + x);
	}

	/** @deprecated */
	public void noSwerve() {
		if (Math.abs(joystick.getRawAxis(1)) < 0.1 && Math.abs(joystick.getRawAxis(4)) < 0.1) {
			leftSide.set(0);
			rightSide.set(0);
		} else if (Math.abs(joystick.getRawAxis(1)) > 0.1 && Math.abs(joystick.getRawAxis(4)) < 0.1) {
			leftSide.set(joystick.getRawAxis(1));
			rightSide.set(joystick.getRawAxis(1));
			joystickOneFirst = true;
		} else if (Math.abs(joystick.getRawAxis(1)) < 0.1 && Math.abs(joystick.getRawAxis(4)) > 0.1) {
			leftSide.set(-joystick.getRawAxis(4));
			rightSide.set(joystick.getRawAxis(4));
			joystickOneFirst = false;
		} else if (joystickOneFirst) {
			leftSide.set(joystick.getRawAxis(1));
			rightSide.set(joystick.getRawAxis(1));
		} else if (!joystickOneFirst) {
			leftSide.set(joystick.getRawAxis(4));
			rightSide.set(-joystick.getRawAxis(4));
		}
	}
}
