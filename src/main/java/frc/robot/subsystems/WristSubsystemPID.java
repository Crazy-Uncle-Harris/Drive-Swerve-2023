// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

import com.revrobotics.SparkMaxAbsoluteEncoder.Type;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;
import com.revrobotics.SparkMaxAbsoluteEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

public class WristSubsystemPID extends SubsystemBase {

  private static WristSubsystemPID m_inst = null;

  private CANSparkMax m_motor;
  private CANSparkMax m_follower;
  private SparkMaxPIDController m_pidController;
  private SparkMaxAbsoluteEncoder m_AbsoluteEncoder;
  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput, targetPosition;
  private int currWristPoseIndex = 0;

  public static WristSubsystemPID getInstance() {
    if (m_inst == null) {
      m_inst = new WristSubsystemPID();
    }
    return m_inst;
  }

  /** Creates a new Wrist. */
  private WristSubsystemPID() {

    m_motor = new CANSparkMax(Constants.WristSubsystemConstants.SparkMaxDeviceID, MotorType.kBrushed);
    m_follower = new CANSparkMax(Constants.WristSubsystemConstants.SparkMaxFollowerDeviceID, MotorType.kBrushless);
    m_motor.restoreFactoryDefaults();
    m_follower.restoreFactoryDefaults();

    m_AbsoluteEncoder = m_motor.getAbsoluteEncoder(Type.kDutyCycle);
    m_AbsoluteEncoder.setPositionConversionFactor(360);
    m_AbsoluteEncoder.setVelocityConversionFactor(1);
    m_AbsoluteEncoder.setInverted(true);
    m_AbsoluteEncoder.setZeroOffset(211.4551878);
  
    m_pidController = m_motor.getPIDController();
    m_pidController.setFeedbackDevice(m_AbsoluteEncoder);
    m_pidController.setPositionPIDWrappingEnabled(true);
    m_pidController.setPositionPIDWrappingMaxInput(360);
    m_pidController.setPositionPIDWrappingMinInput(0);

    m_pidController.setP(0.125);
    m_pidController.setI(0);
    m_pidController.setD(-0.2);
    m_pidController.setFF(0);
    m_pidController.setOutputRange(-1, 1);

    m_motor.setIdleMode(IdleMode.kBrake);
    m_motor.setSmartCurrentLimit(80);
    m_motor.setClosedLoopRampRate(.5);

    m_follower.setIdleMode(IdleMode.kBrake);
    m_follower.setSmartCurrentLimit(80);
    m_follower.setClosedLoopRampRate(.5);

    m_follower.follow(m_motor);
    //m_motor.setSoftLimit(SoftLimitDirection.kForward, 153);
    //m_motor.setSoftLimit(SoftLimitDirection.kReverse, 0);
    // m_motor.enableSoftLimit(SoftLimitDirection.kForward, true);
    // m_motor.enableSoftLimit(SoftLimitDirection.kReverse, true);


    // Save the SPARK MAX configurations. If a SPARK MAX browns out during
    // operation, it will maintain the above configurations.
    m_motor.burnFlash();
    m_follower.burnFlash();

    this.targetPosition = 0;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Wrist Encoder", m_AbsoluteEncoder.getPosition());
    SmartDashboard.putNumber("Wrist target position", targetPosition);

  }

  public void in() {
    m_pidController.setReference(1, CANSparkMax.ControlType.kDutyCycle);
  }

  public void out() {
    m_pidController.setReference(-1, CANSparkMax.ControlType.kDutyCycle);
  }

  public void stop() {
    m_pidController.setReference(0.0, CANSparkMax.ControlType.kDutyCycle);
  }

  public void setPosition(double position){
    m_pidController.setReference(position, CANSparkMax.ControlType.kPosition,0,.1, ArbFFUnits.kPercentOut);
  }

  public void top(){
    this.targetPosition = Constants.WristSubsystemConstants.WristPositions.top;
    this.setPosition(Constants.WristSubsystemConstants.WristPositions.top);
  }

  public void middle(){
    this.targetPosition = Constants.WristSubsystemConstants.WristPositions.middle;
    this.setPosition(Constants.WristSubsystemConstants.WristPositions.middle);
  }

  public void floorCube(){
    this.targetPosition = Constants.WristSubsystemConstants.WristPositions.floorCube;
    this.setPosition(Constants.WristSubsystemConstants.WristPositions.floorCube);
  }

  public void floorCone(){
    this.targetPosition = Constants.WristSubsystemConstants.WristPositions.floorCone;
    this.setPosition(Constants.WristSubsystemConstants.WristPositions.floorCone);
  }

  public void tiltedCone(){
    this.targetPosition = Constants.WristSubsystemConstants.WristPositions.tiltedCone;
    this.setPosition(Constants.WristSubsystemConstants.WristPositions.tiltedCone);
  }

  public void zero(){
    this.targetPosition = 0;
    this.setPosition(0);
  }

  public void stepDown() {

    if (currWristPoseIndex > 0) {
      currWristPoseIndex--;
    }

    this.targetPosition = Constants.WristSubsystemConstants.WristPositions.WristPoses[currWristPoseIndex];
    this.setPosition(Constants.WristSubsystemConstants.WristPositions.WristPoses[currWristPoseIndex]);
  }

  public void stepUp() {

    if (currWristPoseIndex < 4) {
      currWristPoseIndex++;
    }

    this.targetPosition = Constants.WristSubsystemConstants.WristPositions.WristPoses[currWristPoseIndex];
    this.setPosition(Constants.WristSubsystemConstants.WristPositions.WristPoses[currWristPoseIndex]);
  }
}