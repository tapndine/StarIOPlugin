//
//  StarIoExt.h
//  StarIO_Extension
//
//  Created by Yuji on 2015/**/**.
//  Copyright (c) 2015年 Star Micronics. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "ISCBBuilder.h"

@interface StarIoExt : NSObject

+ (NSString *)description;

+ (ISCBBuilder *)createCommandBuilder:(SCBFactoryEmulation)emulation;

@end
