//
//  ISCBBuilder.h
//  StarIO_Extension
//
//  Created by Yuji on 2015/**/**.
//  Copyright (c) 2015年 Star Micronics. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <UIKit/UIKit.h>

typedef NS_ENUM(NSUInteger, SCBFactoryEmulation) {
    SCBFactoryEmulationStar,      // Starｴﾐｭﾚｰｼｮﾝ
    SCBFactoryEmulationEscPos     // ESC/POSｴﾐｭﾚｰｼｮﾝ
};

typedef NS_ENUM(NSUInteger, SCBAlignmentPosition) {
    SCBAlignmentPositionLeft,       // 左揃え
    SCBAlignmentPositionCenter,     // 中央揃え
    SCBAlignmentPositionRight       // 右揃え
};

typedef NS_ENUM(NSUInteger, SCBBitmapConverterRotation) {
    SCBBitmapConverterRotationNormal,       // 通常
    SCBBitmapConverterRotationRight90,      // 右90°回転
    SCBBitmapConverterRotationLeft90,       // 左90°回転
    SCBBitmapConverterRotationRotate180     // 180°回転
};

@interface ISCBBuilder : NSObject

@property (nonatomic, readonly) NSMutableData *commands;

- (void)appendBitmap:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width bothScale:(BOOL)bothScale rotation:(SCBBitmapConverterRotation)rotation;
- (void)appendBitmap:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width                           rotation:(SCBBitmapConverterRotation)rotation;
- (void)appendBitmap:(UIImage *)image diffusion:(BOOL)diffusion                                                  rotation:(SCBBitmapConverterRotation)rotation;
- (void)appendBitmap:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width bothScale:(BOOL)bothScale;
- (void)appendBitmap:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width;
- (void)appendBitmap:(UIImage *)image diffusion:(BOOL)diffusion;

- (void)appendBitmapWithAbsolutePosition:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width bothScale:(BOOL)bothScale rotation:(SCBBitmapConverterRotation)rotation position:(NSInteger)position;
- (void)appendBitmapWithAbsolutePosition:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width                           rotation:(SCBBitmapConverterRotation)rotation position:(NSInteger)position;
- (void)appendBitmapWithAbsolutePosition:(UIImage *)image diffusion:(BOOL)diffusion                                                  rotation:(SCBBitmapConverterRotation)rotation position:(NSInteger)position;
- (void)appendBitmapWithAbsolutePosition:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width bothScale:(BOOL)bothScale                                               position:(NSInteger)position;
- (void)appendBitmapWithAbsolutePosition:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width                                                                         position:(NSInteger)position;
- (void)appendBitmapWithAbsolutePosition:(UIImage *)image diffusion:(BOOL)diffusion                                                                                                position:(NSInteger)position;

- (void)appendBitmapWithAlignment:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width bothScale:(BOOL)bothScale rotation:(SCBBitmapConverterRotation)rotation position:(SCBAlignmentPosition)position;
- (void)appendBitmapWithAlignment:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width                           rotation:(SCBBitmapConverterRotation)rotation position:(SCBAlignmentPosition)position;
- (void)appendBitmapWithAlignment:(UIImage *)image diffusion:(BOOL)diffusion                                                  rotation:(SCBBitmapConverterRotation)rotation position:(SCBAlignmentPosition)position;
- (void)appendBitmapWithAlignment:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width bothScale:(BOOL)bothScale                                               position:(SCBAlignmentPosition)position;
- (void)appendBitmapWithAlignment:(UIImage *)image diffusion:(BOOL)diffusion width:(NSInteger)width                                                                         position:(SCBAlignmentPosition)position;
- (void)appendBitmapWithAlignment:(UIImage *)image diffusion:(BOOL)diffusion                                                                                                position:(SCBAlignmentPosition)position;

@end
