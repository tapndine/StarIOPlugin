//
//  StarIoExtManager.h
//  StarIO_Extension
//
//  Created by Yuji on 2015/**/**.
//  Copyright (c) 2015年 Star Micronics. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <StarIO/SMPort.h>

typedef NS_ENUM(NSUInteger, StarIoExtManagerType) {
    StarIoExtManagerTypeStandard = 0,
    StarIoExtManagerTypeWithBarcodeReader,
    StarIoExtManagerTypeOnlyBarcodeReader,
};

typedef NS_ENUM(NSUInteger, StarIoExtManagerStatus) {
    StarIoExtManagerStatusInvalid = 0,
    StarIoExtManagerStatusImpossible,
    StarIoExtManagerStatusPrinterOnline,
    StarIoExtManagerStatusPrinterOffline,
    StarIoExtManagerStatusPrinterPaperReady,
    StarIoExtManagerStatusPrinterPaperNearEmpty,
    StarIoExtManagerStatusPrinterPaperEmpty,
    StarIoExtManagerStatusPrinterCoverOpen,
    StarIoExtManagerStatusPrinterCoverClose,
    StarIoExtManagerStatusCashDrawerOpen,
    StarIoExtManagerStatusCashDrawerClose,
    StarIoExtManagerStatusBarcodeReaderConnect,
    StarIoExtManagerStatusBarcodeReaderDisconnect
};

@protocol StarIoExtManagerDelegate <NSObject>

@optional

- (void)didPrinterImpossible;

- (void)didPrinterOnline;
- (void)didPrinterOffline;

- (void)didPrinterPaperReady;
- (void)didPrinterPaperNearEmpty;
- (void)didPrinterPaperEmpty;

- (void)didPrinterCoverOpen;
- (void)didPrinterCoverClose;

- (void)didCashDrawerOpen;
- (void)didCashDrawerClose;

- (void)didBarcodeReaderImpossible;

- (void)didBarcodeReaderConnect;
- (void)didBarcodeReaderDisconnect;

- (void)didBarcodeDataReceive:(NSData *)data;

- (void)didAccessoryConnectSuccess;
- (void)didAccessoryConnectFailure;
- (void)didAccessoryDisconnect;

@end

@interface StarIoExtManager : NSObject

@property (readonly, nonatomic) SMPort *port;

@property (readonly, nonatomic) StarIoExtManagerStatus printerOnlineStatus;
@property (readonly, nonatomic) StarIoExtManagerStatus printerPaperReadyStatus;
@property (readonly, nonatomic) StarIoExtManagerStatus printerCoverOpenStatus;
@property (readonly, nonatomic) StarIoExtManagerStatus cashDrawerOpenStatus;
@property (readonly, nonatomic) StarIoExtManagerStatus barcodeReaderConnectStatus;

@property (nonatomic) BOOL cashDrawerOpenActiveHigh;

@property (weak, nonatomic) id<StarIoExtManagerDelegate> delegate;

@property (readonly, nonatomic) NSRecursiveLock *lock;

- (id)initWithType:(StarIoExtManagerType)type portName:(NSString *)portName portSettings:(NSString *)portSettings ioTimeoutMillis:(NSUInteger)ioTimeoutMillis;

- (BOOL)connect;

- (BOOL)disconnect;

@end
