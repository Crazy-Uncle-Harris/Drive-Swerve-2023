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
  private SparkMaxPIDController m_pidController;
  private SparkMaxAbsoluteEncoder m_AbsoluteEncoder;
  public double kP, kI, kD, kIz, kFF, kMaxOutput, kMinOutput;

  public static WristSubsystemPID getInstance() {
    if (m_inst == null) {
      m_inst = new WristSubsystemPID();
    }
    return m_inst;
  }

  /** Creates a new Wrist. */
  private WristSubsystemPID() {

    m_motor = new CANSparkMax(Constants.WristSubsystemConstants.SparkMaxDeviceID, MotorType.kBrushed);
    m_motor.restoreFactoryDefaults();

    m_AbsoluteEncoder = m_motor.getAbsoluteEncoder(Type.kDutyCycle);
    m_AbsoluteEncoder.setPositionConversionFactor(360);
    m_AbsoluteEncoder.setVelocityConversionFactor(1);
    m_AbsoluteEncoder.setInverted(false);
    m_AbsoluteEncoder.setZeroOffset(180);
  
    m_pidController = m_motor.getPIDController();
    m_pidController.setFeedbackDevice(m_AbsoluteEncoder);
    m_pidController.setPositionPIDWrappingEnabled(false);
    m_pidController.setP(0.00995);
    m_pidController.setI(0);
    m_pidController.setD(0);
    m_pidController.setFF(0);
    m_pidController.setOutputRange(-1, 1);

    m_motor.setIdleMode(IdleMode.kBrake);
    m_motor.setSmartCurrentLimit(80);
    m_motor.setClosedLoopRampRate(.5);
    m_motor.setSoftLimit(SoftLimitDirection.kForward, 330);
    m_motor.setSoftLimit(SoftLimitDirection.kReverse, 30);

    // Save the SPARK MAX configurations. If a SPARK MAX browns out during
    // operation, it will maintain the above configurations.
    m_motor.burnFlash();

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Wrist Encoder", m_AbsoluteEncoder.getPosition());
  }

  public void in() {
    m_pidController.setReference(1, CANSparkMax.ControlType.kDutyCycle);
    // m_motor.set(1);
  }

  public void out() {
    m_pidController.setReference(-1, CANSparkMax.ControlType.kDutyCycle);
    // m_motor.set(-1);
  }

  public void stop() {
    m_pidController.setReference(0.0, CANSparkMax.ControlType.kDutyCycle);
    // m_motor.set(0.0);
  }

  public void setPosition(double position){
    m_pidController.setReference(position, CANSparkMax.ControlType.kPosition,0,.1, ArbFFUnits.kPercentOut);
  }

  public void top(){
    this.setPosition(200);
  }

  public void middle(){
    this.setPosition(100);
  }

  public void floorCube(){
    this.setPosition(30);
  }

  public void floorCone(){
    this.setPosition(30);
  }

  public void tiltedCone(){
    this.setPosition(30);
  }
}